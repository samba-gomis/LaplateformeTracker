package controller;

public class LoginController {

    public void login(String username, String password) {

        // Username validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }

        // Password validation
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        // Simple login (hardcoded)
        if (!username.equals("admin") || !password.equals("admin")) {
            throw new IllegalArgumentException("Identifiants incorrects");
        }
    }
}

