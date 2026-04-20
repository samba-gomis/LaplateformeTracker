package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    // FXML fields (match with your FXML)
    @FXML private TextField userInput;
    @FXML private PasswordField passwordInput;
    @FXML private Button validateButton;

    // Validate button click
    @FXML
    public void onRegisterClick() {
        try {
            login(userInput.getText(), passwordInput.getText());
            openMainView();

        } catch (IllegalArgumentException e) {
            System.err.println("Login error: " + e.getMessage());
        }
    }

    // Move focus to password field
    @FXML
    public void onInputClick() {
        passwordInput.requestFocus();
    }

    // Press Enter in password field
    @FXML
    public void onPasswordClick() {
        onRegisterClick();
    }

    // Login logic
    public void login(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (!username.equals("admin") || !password.equals("admin")) {
            throw new IllegalArgumentException("Incorrect username or password.");
        }
    }

    // Open main view
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

            Stage loginStage = (Stage) validateButton.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            System.err.println("Failed to load main view: " + e.getMessage());
        }
    }
}