import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StudentFormController {

    @FXML
    private TextField ageText;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField gradeField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label titleLabel;

    @FXML
    private Button validateBtn;

    @FXML
    void OnValidate(ActionEvent event) {

        try {
            // Get values from input fields
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            // Convert text to numbers
            int age = Integer.parseInt(ageText.getText());
            double grade = Double.parseDouble(gradeField.getText());

            // Display data in console (for testing)
            System.out.println(firstName + " " + lastName + 
                " - Age: " + age + " - Grade: " + grade);

            // Show success message in UI
            titleLabel.setText("Student added successfully!");

        } catch (NumberFormatException e) {
            // Error if age or grade is not a number
            titleLabel.setText("Age or grade is invalid");

        } catch (Exception e) {
            // General error
            titleLabel.setText("An error occurred");
        }
    }
}