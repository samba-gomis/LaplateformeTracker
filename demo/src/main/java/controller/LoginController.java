package controller;


import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {


    // FXML fields (must match fx:id in FXML)
    @FXML
    private TextField userInput;


    @FXML
    private PasswordField passwordInput;


    // Called when clicking "Validate" button
    @FXML
    public void OnRegisterClick() {


        try {
            login(userInput.getText(), passwordInput.getText());
            System.out.println("Login successful!");


        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    // Called when pressing Enter in username field
    @FXML
    public void OnInputClick() {
        System.out.println("Username entered: " + userInput.getText());
    }


    // Called when pressing Enter in password field
    @FXML
    public void OnPasswordClick() {
        System.out.println("Password field submitted");
        OnRegisterClick(); // optional: trigger login directly
    }


    // Simple authentication (for school project)
    public void login(String username, String password) {

        // Vérification username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        // Vérification password
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Login simple (hardcoded)
        if (!username.equals("admin") || !password.equals("admin")) {
            throw new IllegalArgumentException("Identifiants incorrects");
        }
    }
}

