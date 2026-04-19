package controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.StudentDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.Initializable;
import javax.xml.bind.Unmarshaller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import model.Student;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MainController implements Initializable{

    @FXML private Button addBtn;
    @FXML private PieChart agePieChart;
    @FXML private CheckBox autoSaveToggle;
    @FXML private Label avgLabel;
    @FXML private TableColumn<Student, Integer> colAge;
    @FXML private TableColumn<Student, Double> colGrade;
    @FXML private TableColumn<Student, Integer> colID;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colSurname;
    @FXML private MenuItem csvExport;
    @FXML private Button deleteBtn;
    @FXML private MenuItem exportCsv;
    @FXML private MenuItem exportJSON;
    @FXML private MenuItem exportXML;
    @FXML private BarChart<String, Integer> gradesBarChart;
    @FXML private MenuItem htmlExport;
    @FXML private MenuItem importCVS;
    @FXML private MenuItem importJSON;
    @FXML private MenuItem importXML;
    @FXML private MenuItem itemSortAge;
    @FXML private MenuItem itemSortGPA;
    @FXML private MenuItem itemSortID;
    @FXML private MenuItem itemSortName;
    @FXML private MenuItem itemSortSurname;
    @FXML private MenuItem logOutBtn;
    @FXML private Button modifyBtn;
    @FXML private Pagination pagination;
    @FXML private MenuItem pdfExport;
    @FXML private Button searchBtn;
    @FXML private TextField searchField;
    @FXML private MenuButton sortMenuButton;
    @FXML private TableView<Student> studentTable;
    @FXML private Label totalStudentsLabel;
    @FXML private Label errorLabel;
    private ObservableList<Student> masterData = FXCollections.observableArrayList();
    private FilteredList<Student> filteredData;


    @FXML void onAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentFormView.fxml")); //Load fxml to change the page
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add a new Student");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            updatePieChart();
            updateBarChart();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDelete(ActionEvent event) {
        Student selectedStudent=studentTable.getSelectionModel().getSelectedItem(); //Select a student

        if(selectedStudent!=null){
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION); //Use alert to get a confirmation
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Delete Student : "+selectedStudent.getFirstName());
            alert.setContentText("Are you sure? This action is irreversible");

            if(alert.showAndWait().get()==ButtonType.OK){ //Check if user choose ok
                try {
                    StudentDao.deleteStudent(selectedStudent.getId()); //Use the DAO model to delete a student
                    studentTable.getItems().remove(selectedStudent); //remove from the table
                    updatePieChart();
                    updateBarChart();

                } catch (Exception e) {
                    errorLabel.setText("Error while deleting");
                }
            }

        } else {
            Alert alert=new Alert(Alert.AlertType.WARNING); //Set alert to warning
            alert.setContentText("Please choose a student in the list");
            alert.show();
        }

    }

    @FXML
    void onExportCSV(ActionEvent event) {
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files","*.csv"));
        File file=fileChooser.showSaveDialog(null);

        if(file != null){
            try (PrintWriter printWriter=new PrintWriter(file)){
                printWriter.println("ID;Name;Surname;Age;Grade"); //CSV table format
                List<Student> list=studentTable.getItems();
                for(Student students:list){
                    printWriter.println(students.getId()+";"+students.getFirstName()+";"+students.getLastName()+";"+students.getAge()+";"+students.getGrade());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    @FXML
    void onExportHtml(ActionEvent event) {
        FileChooser fileChooser=new FileChooser(); //Choose the path
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files","*.html"));
        File file = fileChooser.showSaveDialog(null);

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("<html><body><table border='1'>");
            writer.println("<tr><th>ID</th><th>Nom</th><th>Prénom</th><th>Note</th></tr>");

            for (Student s : studentTable.getItems()) {
                writer.println("<tr>");
                writer.println("<td>" + s.getId() + "</td>");
                writer.println("<td>" + s.getLastName() + "</td>");
                writer.println("<td>" + s.getFirstName() + "</td>");
                writer.println("<td>" + s.getGrade() + "</td>");
                writer.println("</tr>");
            }
            writer.println("</table></body></html>");
        } catch (Exception e) { e.printStackTrace(); }

    }

    @FXML
    void onExportJSON(ActionEvent event) {
        FileChooser fileChooser=new FileChooser(); //Choose the path
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files","*.json")); //Set the type in JSON
        File file=fileChooser.showSaveDialog(null);

        if(file!=null){
            try (FileWriter fileWriter=new FileWriter(file)){ //Will write the data in JSON

                Gson gson=new GsonBuilder().setPrettyPrinting().create(); //Use of Gson library to import in JSON and sert of pretty print
                List<Student> list=studentTable.getItems();  //Get the items of the table
                gson.toJson(list, fileWriter); //Write in JSON

            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @XmlRootElement(name = "students") //Class method for the wrapper, for XML format
    public class StudentListWrapper {
        private List<Student> students;

        @XmlElement(name = "student") //Getter of XML to get the students
        public List<Student> getStudents() {
            return students;
        }
        public void setStudents(List<Student> students) { //Setter XML
            this.students = students;
        }
    }

    @FXML
    void onExportXML(ActionEvent event) {
        FileChooser fileChooser=new FileChooser(); //choose the path folder
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files","*.xml")); //set extension
        File file=fileChooser.showSaveDialog(null);

        if(file!=null){ //Check if file isn't empty
            try {
                JAXBContext context=JAXBContext.newInstance(StudentListWrapper.class); //Create a context for the marshaller
                Marshaller marshaller=context.createMarshaller(); //Marshaller instantiation, will convert to XML
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true); //Enable pretty print

                StudentListWrapper studentListWrapper=new StudentListWrapper(); //Instance the class who had wrapped the list of students
                studentListWrapper.setStudents(studentTable.getItems());  //Place the list from Student class into the wrapped class
                marshaller.marshal(studentListWrapper,file); //Set the marshaller

            } catch (JAXBException e) { //Exception for JAXB
                e.printStackTrace();
            }
        }

    }
    @FXML
    void onImportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                Student s = new Student(data[1], data[2], Integer.parseInt(data[3]), Double.parseDouble(data[4]));
                studentTable.getItems().add(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void onImportJSON(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Student>>(){}.getType();
            List<Student> students = gson.fromJson(reader, listType);

            studentTable.getItems().addAll(students);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void onImportXML(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        try {
            JAXBContext context = JAXBContext.newInstance(StudentListWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StudentListWrapper wrapper = (StudentListWrapper) unmarshaller.unmarshal(file);

            studentTable.getItems().addAll(wrapper.getStudents());
        } catch (Exception e) { e.printStackTrace(); }

    }

    @FXML
    void onLogOut(ActionEvent event) {

        if(logOutBtn!=null){
            try {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
                Parent root=loader.load();

                Stage stage=new Stage();
                stage.setTitle("Login");
                stage.setScene(new Scene(root));
                stage.show();

                Stage currentScene=(Stage) studentTable.getScene().getWindow();
                currentScene.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @FXML
    void onModify(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentFormView.fxml"));
                Parent root = loader.load();
                StudentFormController controller = loader.getController();

                Stage stage = new Stage();
                stage.setTitle("Modify a student");
                stage.setScene(new Scene(root));
                stage.show();
                updatePieChart();
                updateBarChart();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Select a student first!");
            alert.show();
        }
    }
    @FXML
    void onSearch(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();

        filteredData.setPredicate(student -> {

            if (searchText.isEmpty()) {
                return true;
            }

            if (student.getFirstName().toLowerCase().contains(searchText)) return true;
            if (student.getLastName().toLowerCase().contains(searchText)) return true;
            return String.valueOf(student.getId()).contains(searchText);
        });
    }

    @FXML
    void onReset(ActionEvent event) {
        searchField.clear();
        filteredData.setPredicate(s -> true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filteredData = new FilteredList<>(masterData, p -> true);


        colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));

        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);

        updatePieChart();
        updateBarChart();
    }

    //All sort methods
    @FXML
    void onSortByAge(ActionEvent event) {
        studentTable.getSortOrder().clear();
        studentTable.getSortOrder().add(colAge);

        colAge.setSortType(TableColumn.SortType.ASCENDING);
        studentTable.sort();


    }

    @FXML
    void onSortByGPA(ActionEvent event) {
        studentTable.getSortOrder().clear();
        studentTable.getSortOrder().add(colGrade);

        colAge.setSortType(TableColumn.SortType.ASCENDING);
        studentTable.sort();

    }

    @FXML
    void onSortByID(ActionEvent event) {
        studentTable.getSortOrder().clear();
        studentTable.getSortOrder().add(colID);

        colAge.setSortType(TableColumn.SortType.ASCENDING);
        studentTable.sort();

    }

    @FXML
    void onSortByName(ActionEvent event) {
        studentTable.getSortOrder().clear();
        studentTable.getSortOrder().add(colName);

        colName.setSortType(TableColumn.SortType.ASCENDING);
        studentTable.sort();

    }

    @FXML
    void onSortBySurname(ActionEvent event) {
        studentTable.getSortOrder().clear();
        studentTable.getSortOrder().add(colSurname);

        colSurname.setSortType(TableColumn.SortType.ASCENDING);
        studentTable.sort();

    }

    @FXML
    void onToggleAutoSave(ActionEvent event) {

        boolean isAutoSaveEnabled = autoSaveToggle.isSelected();

        if (isAutoSaveEnabled) {
            System.out.println("Auto-save enabled!");
        } else {
            System.out.println("Auto-save off");
        }
    }

    private void updatePieChart() {

        agePieChart.getData().clear();

        int underage = 0;
        int legalAge = 0;

        for (Student s : masterData) {
            if (s.getAge() < 18) underage++; //add to underage if students are not more than 18
            else legalAge++;
        }
        agePieChart.getData().add(new PieChart.Data("Underage", underage)); //update the pie chart
        agePieChart.getData().add(new PieChart.Data("Adults", legalAge));

        totalStudentsLabel.setText("Total: " + masterData.size());

        double sum = 0;
        for(Student s : masterData) sum += s.getGrade();
        double avg = masterData.isEmpty() ? 0 : sum / masterData.size();
        avgLabel.setText(String.format("Average: %.2f", avg)); //Auto text of average of the whole class
    }

    private void updateBarChart() {
        gradesBarChart.getData().clear();

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Grades Distribution"); //Set name of the bar

        int countA = 0; //counts initializing
        int countB = 0;
        int countC = 0;

        for (Student s : masterData) { //students in masterData
            if (s.getGrade() >= 15) countA++; //sort by grade
            else if (s.getGrade() >= 10) countB++;
            else countC++;
        }

        series.getData().add(new XYChart.Data<>("15-20", countA)); //Add data to the bar
        series.getData().add(new XYChart.Data<>("10-15", countB));
        series.getData().add(new XYChart.Data<>("0-10", countC));

        gradesBarChart.getData().add(series);
    }

}