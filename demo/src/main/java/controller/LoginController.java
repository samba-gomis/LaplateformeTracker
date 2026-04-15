package controller;

public class LoginController {

    public void login(String username, String password) {

        // Vérification username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }

        // Vérification password
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        // Login simple (hardcoded)
        if (!username.equals("admin") || !password.equals("admin")) {
            throw new IllegalArgumentException("Identifiants incorrects");
        }
    }
}

