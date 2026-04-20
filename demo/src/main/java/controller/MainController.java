package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import database.StudentDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Student;
import controller.StudentFormController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    // FXML fields — table
    @FXML private TableView<Student>        studentTable;
    @FXML private TableColumn<Student, Integer> colID;
    @FXML private TableColumn<Student, String>  colName;
    @FXML private TableColumn<Student, String>  colSurname;
    @FXML private TableColumn<Student, Integer> colAge;
    @FXML private TableColumn<Student, Double>  colGrade;

    // FXML fields — toolbar
    @FXML private TextField  searchField;
    @FXML private Button     searchBtn;
    @FXML private Button     addBtn;
    @FXML private Button     deleteBtn;
    @FXML private Button     modifyBtn;
    @FXML private Label      errorLabel;
    @FXML private MenuItem   logOutBtn;
    @FXML private Pagination pagination;

    // FXML fields — analysis tab
    @FXML private PieChart agePieChart;
    @FXML private BarChart<String, Number> gradesBarChart;
    @FXML private Label    avgLabel;
    @FXML private Label    totalStudentsLabel;
    @FXML private CheckBox autoSaveToggle;

    // Internal state
    private final StudentDao dao = new StudentDao();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private Timer autoSaveTimer;

    // Initialise controller after FXML is loaded
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind table columns to Student fields
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // Load all students from database
        loadStudentsFromDatabase();

        // Hide error label by default
        errorLabel.setVisible(false);
    }

    // Load all students from the database into the table
    private void loadStudentsFromDatabase() {
        studentList = FXCollections.observableArrayList(dao.getAllStudents());
        studentTable.setItems(studentList);
        updateCharts();
        updateStatsLabels();
    }

    // ADD
    @FXML
    void onAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentFormView.fxml"));
            Parent root = loader.load();

            StudentFormController controller = loader.getController();
            controller.setMode("add", null, this); // pass reference so form can refresh table

            Stage stage = new Stage();
            stage.setTitle("Add a student");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Failed to open add form");
        }
    }

    // MODIFY
    @FXML
    void onModify(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentFormView.fxml"));
                Parent root = loader.load();

                StudentFormController controller = loader.getController();
                controller.setMode("edit", selected, this); // pass the selected student to pre-fill the form

                Stage stage = new Stage();
                stage.setTitle("Modify a student");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                showError("Failed to open edit form");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a student in the list first.");
            alert.show();
        }
    }

    // DELETE
    @FXML
    void onDelete(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Delete student: " + selected.getFullName());
            alert.setContentText("Are you sure? This action is irreversible.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    // FIX: dao instance call instead of static StudentDao.deleteStudent()
                    boolean success = dao.deleteStudent(selected.getId());
                    if (success) {
                        studentList.remove(selected);
                        updateCharts();
                        updateStatsLabels();
                    } else {
                        showError("Could not delete student from database.");
                    }
                } catch (Exception e) {
                    showError("Error while deleting: " + e.getMessage());
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a student in the list first.");
            alert.show();
        }
    }

    // SEARCH
    @FXML
    void onSearch(ActionEvent event) {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            studentTable.setItems(studentList);
            return;
        }

        FilteredList<Student> filtered = new FilteredList<>(studentList, student ->
            student.getFirstName().toLowerCase().contains(query) ||
            student.getLastName().toLowerCase().contains(query) ||
            String.valueOf(student.getId()).contains(query)
        );

        studentTable.setItems(filtered);
    }

    // RESET search
    @FXML
    void onReset(ActionEvent event) {
        searchField.clear();
        studentTable.setItems(studentList);
    }

    // SORT
    @FXML void onSortByID(ActionEvent event)      { studentList.sort(Comparator.comparingInt(Student::getId)); }
    @FXML void onSortByName(ActionEvent event)    { studentList.sort(Comparator.comparing(Student::getFirstName)); }
    @FXML void onSortBySurname(ActionEvent event) { studentList.sort(Comparator.comparing(Student::getLastName)); }
    @FXML void onSortByAge(ActionEvent event)     { studentList.sort(Comparator.comparingInt(Student::getAge)); }
    @FXML void onSortByGPA(ActionEvent event)     { studentList.sort(Comparator.comparingDouble(Student::getGrade)); }

    // LOG OUT
    @FXML
    void onLogOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window using the table's scene reference
            Stage currentStage = (Stage) studentTable.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            showError("Failed to log out: " + e.getMessage());
        }
    }

    // AUTO SAVE
    @FXML
    void onToggleAutoSave(ActionEvent event) {
        if (autoSaveToggle.isSelected()) {
            autoSaveTimer = new Timer(true);
            autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Auto-save: reload from DB and export to backup CSV
                    System.out.println("Auto-save triggered...");
                    exportToCSVFile(new File("autosave_backup.csv"));
                }
            }, 0, 60_000); // every 60 seconds
            System.out.println("Auto-save enabled.");
        } else {
            if (autoSaveTimer != null) autoSaveTimer.cancel();
            System.out.println("Auto-save disabled.");
        }
    }

    // EXPORT CSV
    @FXML
    void onExportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) exportToCSVFile(file);
    }

    private void exportToCSVFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("ID;FirstName;LastName;Age;Grade");
            for (Student s : studentTable.getItems()) {
                writer.println(s.getId() + ";" + s.getFirstName() + ";" +
                               s.getLastName() + ";" + s.getAge() + ";" + s.getGrade());
            }
        } catch (Exception e) {
            showError("Export CSV failed: " + e.getMessage());
        }
    }

    // EXPORT HTML
    @FXML
    void onExportHtml(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files", "*.html"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("<html><body><table border='1'>");
                writer.println("<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Age</th><th>Grade</th></tr>");
                for (Student s : studentTable.getItems()) {
                    writer.println("<tr><td>" + s.getId() + "</td><td>" + s.getFirstName() +
                                   "</td><td>" + s.getLastName() + "</td><td>" + s.getAge() +
                                   "</td><td>" + s.getGrade() + "</td></tr>");
                }
                writer.println("</table></body></html>");
            } catch (Exception e) {
                showError("Export HTML failed: " + e.getMessage());
            }
        }
    }

    // EXPORT JSON
    @FXML
    void onExportJSON(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(studentTable.getItems(), fileWriter);
            } catch (Exception e) {
                showError("Export JSON failed: " + e.getMessage());
            }
        }
    }

    // EXPORT XML
    @FXML
    void onExportXML(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(StudentListWrapper.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                StudentListWrapper wrapper = new StudentListWrapper();
                wrapper.setStudents(new ArrayList<>(studentTable.getItems()));
                marshaller.marshal(wrapper, file);

            } catch (JAXBException e) {
                showError("Export XML failed: " + e.getMessage());
            }
        }
    }

    // IMPORT CSV
    @FXML
    void onImportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine(); // skip header line
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    Student s = new Student(data[1], data[2],
                                            Integer.parseInt(data[3]),
                                            Double.parseDouble(data[4]));
                    // FIX: also save to database, not just the table view
                    boolean saved = dao.addStudent(s);
                    if (saved) studentList.add(s);
                }
                updateCharts();
                updateStatsLabels();
            } catch (Exception e) {
                showError("Import CSV failed: " + e.getMessage());
            }
        }
    }

    // IMPORT JSON
    @FXML
    void onImportJSON(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (Reader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Student>>(){}.getType();
                List<Student> students = gson.fromJson(reader, listType);

                for (Student s : students) {
                    boolean saved = dao.addStudent(s);
                    if (saved) studentList.add(s);
                }
                updateCharts();
                updateStatsLabels();
            } catch (Exception e) {
                showError("Import JSON failed: " + e.getMessage());
            }
        }
    }

    // IMPORT XML
    @FXML
    void onImportXML(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(StudentListWrapper.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                StudentListWrapper wrapper = (StudentListWrapper) unmarshaller.unmarshal(file);

                for (Student s : wrapper.getStudents()) {
                    boolean saved = dao.addStudent(s);
                    if (saved) studentList.add(s);
                }
                updateCharts();
                updateStatsLabels();
            } catch (Exception e) {
                showError("Import XML failed: " + e.getMessage());
            }
        }
    }

    // CHARTS
    // Called by StudentFormController after add/edit to refresh the table and charts
    public void refreshTable() {
        loadStudentsFromDatabase();
    }

    private void updateCharts() {
        updatePieChart();
        updateBarChart();
    }

    private void updatePieChart() {
        Map<String, Integer> ageGroups = new LinkedHashMap<>();
        ageGroups.put("< 18",  0);
        ageGroups.put("18-22", 0);
        ageGroups.put("23-27", 0);
        ageGroups.put("> 27",  0);

        for (Student s : studentList) {
            int age = s.getAge();
            if (age < 18)       ageGroups.merge("< 18",  1, Integer::sum);
            else if (age <= 22) ageGroups.merge("18-22", 1, Integer::sum);
            else if (age <= 27) ageGroups.merge("23-27", 1, Integer::sum);
            else                ageGroups.merge("> 27",  1, Integer::sum);
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        ageGroups.forEach((label, count) -> {
            if (count > 0) pieData.add(new PieChart.Data(label + " (" + count + ")", count));
        });
        agePieChart.setData(pieData);
    }

    private void updateBarChart() {
        gradesBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Average grade");

        if (!studentList.isEmpty()) {
            double avg = studentList.stream()
                .mapToDouble(Student::getGrade)
                .average()
                .orElse(0.0);
            series.getData().add(new XYChart.Data<>("All students", avg));
        }

        gradesBarChart.getData().add(series);
    }

    private void updateStatsLabels() {
        int total = studentList.size();
        totalStudentsLabel.setText("Total students: " + total);

        if (total > 0) {
            double avg = studentList.stream()
                .mapToDouble(Student::getGrade)
                .average()
                .orElse(0.0);
            avgLabel.setText(String.format("Class average: %.2f / 20", avg));
        } else {
            avgLabel.setText("Class average: N/A");
        }
    }

    // HELPERS

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    // XML wrapper inner class
    @XmlRootElement(name = "students")
    public static class StudentListWrapper {
        private List<Student> students;

        @XmlElement(name = "student")
        public List<Student> getStudents() { return students; }
        public void setStudents(List<Student> students) { this.students = students; }
    }
}