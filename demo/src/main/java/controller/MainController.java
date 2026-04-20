package controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.StudentDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {

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
            // General error
            titleLabel.setText("An error occurred");
        }
    }
}"
