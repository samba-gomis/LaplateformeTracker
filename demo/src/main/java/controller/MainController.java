package controller;

import database.StudentDao;
import model.Student;
import model.FileManagement;
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    // FXML fields table
    @FXML private TableView<Student>        studentTable;
    @FXML private TableColumn<Student, Integer> colID;
    @FXML private TableColumn<Student, String>  colName;
    @FXML private TableColumn<Student, String>  colSurname;
    @FXML private TableColumn<Student, Integer> colAge;
    @FXML private TableColumn<Student, Double>  colGrade;

    // FXML fields toolbar
    @FXML private TextField  searchField;
    @FXML private Button     searchBtn, addBtn, deleteBtn, modifyBtn;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        loadStudentsFromDatabase();
        errorLabel.setVisible(false);
    }

    private void loadStudentsFromDatabase() {
        studentList = FXCollections.observableArrayList(dao.getAllStudents());
        studentTable.setItems(studentList);
        updateCharts();
        updateStatsLabels();
    }

    //Actions : Add/Modify/Delete

    @FXML
    void onAdd(ActionEvent event) {
        openForm("add", null, "Add a student");
    }

    @FXML
    void onModify(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openForm("edit", selected, "Modify a student");
        } else {
            showSimpleAlert("Please select a student in the list first.");
        }
    }

    private void openForm(String mode, Student student, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentFormView.fxml"));
            Parent root = loader.load();
            StudentFormController controller = loader.getController();
            controller.setMode(mode, student, this);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { showError("Failed to open form"); }
    }

    @FXML
    void onDelete(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete student: " + selected.getFullName() + "?", ButtonType.OK, ButtonType.CANCEL);
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (dao.deleteStudent(selected.getId())) {
                    studentList.remove(selected);
                    updateCharts();
                    updateStatsLabels();
                } else { showError("Could not delete from database."); }
            }
        }
    }

    //Search
    @FXML
    void onSearch(ActionEvent event) {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { studentTable.setItems(studentList); return; }
        FilteredList<Student> filtered = new FilteredList<>(studentList, s ->
                s.getFirstName().toLowerCase().contains(query) || s.getLastName().toLowerCase().contains(query) || String.valueOf(s.getId()).contains(query)
        );
        studentTable.setItems(filtered);
    }

    @FXML void onReset(ActionEvent event) { searchField.clear(); studentTable.setItems(studentList); }

    //Sorts methods
    @FXML void onSortByID(ActionEvent e)      { studentList.sort(Comparator.comparingInt(Student::getId)); }
    @FXML void onSortByName(ActionEvent e)    { studentList.sort(Comparator.comparing(Student::getFirstName)); }
    @FXML void onSortBySurname(ActionEvent e) { studentList.sort(Comparator.comparing(Student::getLastName)); }
    @FXML void onSortByAge(ActionEvent e)     { studentList.sort(Comparator.comparingInt(Student::getAge)); }
    @FXML void onSortByGPA(ActionEvent e)     { studentList.sort(Comparator.comparingDouble(Student::getGrade)); }

    //Logout

    @FXML
    void onLogOut(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            ((Stage) studentTable.getScene().getWindow()).setScene(new Scene(root));
        } catch (Exception e) { showError("Failed to log out"); }
    }

    //Autosave
    @FXML
    void onToggleAutoSave(ActionEvent event) {
        if (autoSaveToggle.isSelected()) {
            autoSaveTimer = new Timer(true);
            autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
                @Override public void run() { try { FileManagement.exportCSV(new File("autosave_backup.csv"), studentList); } catch (Exception ignored) {} }
            }, 0, 60_000);
        } else if (autoSaveTimer != null) { autoSaveTimer.cancel(); }
    }

    //Imports/Exports

    @FXML void onExportCSV(ActionEvent e) { File f = getFileSave("CSV", "*.csv"); if (f != null) try { FileManagement.exportCSV(f, studentList); } catch (Exception ex) { showError("Export failed"); } }
    @FXML void onExportJSON(ActionEvent e) { File f = getFileSave("JSON", "*.json"); if (f != null) try { FileManagement.exportJSON(f, studentList); } catch (Exception ex) { showError("Export failed"); } }
    @FXML void onExportXML(ActionEvent e) { File f = getFileSave("XML", "*.xml"); if (f != null) try { FileManagement.exportXML(f, studentList); } catch (Exception ex) { showError("Export failed"); } }
    @FXML void onExportHtml(ActionEvent e) { File f = getFileSave("HTML", "*.html"); if (f != null) try { FileManagement.exportHTML(f, studentList); } catch (Exception ex) { showError("Export failed"); } }

    @FXML void onImportCSV(ActionEvent e) { File f = getFileOpen(); if (f != null) try { processImport(FileManagement.importCSV(f)); } catch (Exception ex) { showError("Import failed"); } }
    @FXML void onImportJSON(ActionEvent e) { File f = getFileOpen(); if (f != null) try { processImport(FileManagement.importJSON(f)); } catch (Exception ex) { showError("Import failed"); } }
    @FXML void onImportXML(ActionEvent e) { File f = getFileOpen(); if (f != null) try { processImport(FileManagement.importXML(f)); } catch (Exception ex) { showError("Import failed"); } }

    private void processImport(List<Student> students) {
        for (Student s : students) { if (dao.addStudent(s)) studentList.add(s); }
        updateCharts(); updateStatsLabels();
    }

    //Charts and stats

    public void refreshTable() { loadStudentsFromDatabase(); }

    private void updateCharts() {
        updatePieChart();
        updateBarChart();
    }

    private void updatePieChart() {
        int g1=0, g2=0, g3=0, g4=0;
        for (Student s : studentList) {
            int a = s.getAge();
            if (a < 18) g1++; else if (a <= 22) g2++; else if (a <= 27) g3++; else g4++;
        }
        agePieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("<18", g1), new PieChart.Data("18-22", g2), new PieChart.Data("23-27", g3), new PieChart.Data(">27", g4)
        ));
    }

    private void updateBarChart() {
        gradesBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Average grade");
        if (!studentList.isEmpty()) {
            series.getData().add(new XYChart.Data<>("All students", studentList.stream().mapToDouble(Student::getGrade).average().orElse(0.0)));
        }
        gradesBarChart.getData().add(series);
    }

    private void updateStatsLabels() {
        totalStudentsLabel.setText("Total students: " + studentList.size());
        double avg = studentList.stream().mapToDouble(Student::getGrade).average().orElse(0.0);
        avgLabel.setText(studentList.isEmpty() ? "Class average: N/A" : String.format("Class average: %.2f / 20", avg));
    }

    //Helpers

    private File getFileSave(String desc, String ext) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, ext));
        return fc.showSaveDialog(null);
    }

    private File getFileOpen() { return new FileChooser().showOpenDialog(null); }

    private void showError(String msg) { errorLabel.setText(msg); errorLabel.setVisible(true); }

    private void showSimpleAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setContentText(msg);
        a.show();
    }

    @XmlRootElement(name = "students")
    public static class StudentListWrapper {
        private List<Student> students;
        @XmlElement(name = "student") public List<Student> getStudents() { return students; }
        public void setStudents(List<Student> students) { this.students = students; }
    }
}