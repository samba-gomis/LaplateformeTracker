import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadListener;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import javax.xml.bind.JAXB;
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

public class MainController {

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

    @FXML void onAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentFormView.fxml")); //Load fxml to change the page
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add a new Student");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

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
                    StudentDao.deleteStudent(selectedStudent.getStudentById()); //Use the DAO model to delete a student
                    studentTable.getItems().remove(selectedStudent); //remove from the table
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


    @FXML
    void onExportPdf(ActionEvent event) {

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

    }

    @FXML
    void onImportJSON(ActionEvent event) {

    }

    @FXML
    void onImportXML(ActionEvent event) {

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
        Student selected=studentTable.getSelectionModel().getSelectedItem(); //Take the student from the table

        if(selected!=null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentFormView.fxml")); //Load the Form view
                Parent root = loader.load();

                StudentFormController controller=loader.getController(); //Take the controller of the view
                controller.prepareFields(selected);

                Stage stage = new Stage();
                stage.setTitle("Modify a student");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e){
                e.printStackTrace();
            }
        } Alert alert=new Alert(Alert.AlertType.WARNING); //Error gestion
          alert.setContentText("Select a student first!");
          alert.show();
    }

    @FXML
    void onSearch(ActionEvent event) {

    }

    @FXML
    void onSortByAge(ActionEvent event) {

    }

    @FXML
    void onSortByGPA(ActionEvent event) {

    }

    @FXML
    void onSortByID(ActionEvent event) {

    }

    @FXML
    void onSortByName(ActionEvent event) {

    }

    @FXML
    void onSortBySurname(ActionEvent event) {

    }

    @FXML
    void onToggleAutoSave(ActionEvent event) {

    }

}
