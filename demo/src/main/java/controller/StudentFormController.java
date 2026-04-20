package controller;

import database.StudentDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Student;

public class StudentFormController {

    // FXML fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField ageText;
    @FXML private TextField gradeField;
    @FXML private Label     titleLabel;
    @FXML private Button    validateBtn;

    // Internal state
    private final StudentDao dao = new StudentDao();
    private String           mode;           // "add" or "edit"
    private Student          studentToEdit;  // null if mode is "add"
    private MainController   mainController; // reference to refresh the table after save

    // Called by MainController before showing this window
    // Sets the mode of the form and pre-fills fields if editing.
     
    public void setMode(String mode, Student student, MainController mainController) {
        this.mode           = mode;
        this.studentToEdit  = student;
        this.mainController = mainController;

        if ("edit".equals(mode) && student != null) {
            // Pre-fill the form fields with the selected student's data
            titleLabel.setText("Edit student");
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            ageText.setText(String.valueOf(student.getAge()));
            gradeField.setText(String.valueOf(student.getGrade()));
        } else {
            titleLabel.setText("Add a student");
        }
    }

    // Validate button click
    @FXML
    void OnValidate(ActionEvent event) {
        try {
            // Get and validate input values
            String firstName = firstNameField.getText().trim();
            String lastName  = lastNameField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                titleLabel.setText("First name and last name are required.");
                return;
            }

            int    age   = Integer.parseInt(ageText.getText().trim());
            double grade = Double.parseDouble(gradeField.getText().trim());

            if (age <= 0 || age > 120) {
                titleLabel.setText("Age must be between 1 and 120.");
                return;
            }

            if (grade < 0 || grade > 20) {
                titleLabel.setText("Grade must be between 0 and 20.");
                return;
            }

            // Save to database depending on mode
            boolean success;

            if ("edit".equals(mode) && studentToEdit != null) {
                // UPDATE existing student
                studentToEdit.setFirstName(firstName);
                studentToEdit.setLastName(lastName);
                studentToEdit.setAge(age);
                studentToEdit.setGrade(grade);

                success = dao.updateStudent(studentToEdit);
                if (success) {
                    titleLabel.setText("Student updated successfully!");
                } else {
                    titleLabel.setText("Failed to update student in database.");
                    return;
                }

            } else {
                // INSERT new student
                Student newStudent = new Student(firstName, lastName, age, grade);
                success = dao.addStudent(newStudent);
                if (success) {
                    titleLabel.setText("Student added successfully!");
                    clearFields();
                } else {
                    titleLabel.setText("Failed to save student to database.");
                    return;
                }
            }

            // Refresh the main table and close this window
            if (mainController != null) {
                mainController.refreshTable();
            }

            // Close the form window after a short delay so the user sees the success message
            Stage stage = (Stage) validateBtn.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            titleLabel.setText("Age and grade must be valid numbers.");

        } catch (Exception e) {
            titleLabel.setText("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Clear all input fields (used after a successful add)
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        ageText.clear();
        gradeField.clear();
    }
}