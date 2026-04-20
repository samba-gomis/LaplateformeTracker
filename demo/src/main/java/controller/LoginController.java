package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    // FXML fields
    @FXML private PasswordField passwordInput;
    @FXML private Button        validateButton;
    @FXML private Label         errorLabel;  

    // Validate button click
    @FXML
    public void onRegisterClick() {
        try {
            login(userInput.getText(), passwordInput.getText());
            openMainView();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // Enter key in username field, move focus to password
    public void onInputClick() {
        passwordInput.requestFocus();
    }

    // Enter key in password field, trigger login
    @FXML
    public void onPasswordClick() {
        onRegisterClick();
    }

    // Authentication logic
    // Validates credentials. Throws IllegalArgumentException if invalid
    
    public void login(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        // Hardcoded credentials — replace with DB check for production
        if (!username.equals("admin") || !password.equals("admin")) {
            throw new IllegalArgumentException("Incorrect username or password.");
        }
    }

    // Navigate to MainView after successful login
    // Loads MainView.fxml, opens the main window and closes the login window
    private void openMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();

            Stage mainStage = new Stage();
            mainStage.setTitle("La Plateforme Tracker");
            mainStage.setScene(new Scene(root));
            mainStage.setMinWidth(800);
            mainStage.setMinHeight(500);
            mainStage.show();

            // Close the login window
            Stage loginStage = (Stage) validateButton.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            showError("Failed to load main view: " + e.getMessage());
        }
    }

    // Show an error message in the UI

    private void showError(String message) {
        System.err.println("Login error: " + message);

        // Show in UI if errorLabel is wired in the FXML
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }

        // Clear the password field for security
        passwordInput.clear();
    }
}