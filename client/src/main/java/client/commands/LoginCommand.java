package client.commands;

import client.auth.AuthManager;
import client.managers.InputManager;

public class LoginCommand implements Command {
    private final java.util.function.BiFunction<String, String, Boolean> authAction;

    public LoginCommand(java.util.function.BiFunction<String, String, Boolean> authAction) {
        this.authAction = authAction;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getDescription() {
        return "Authenticate with login and password";
    }

    @Override
    public void execute(String[] args) {
        if (AuthManager.isAuthenticated()) {
            System.out.println("Already authenticated.");
            return;
        }

        String login = InputManager.readLine("Login: ");
        String password = InputManager.readLine("Password: ");
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Login and password cannot be empty.");
            return;
        }

        AuthManager.setCredentials(login, password);
        boolean success = authAction.apply(login, password);
        if (success) {
            AuthManager.setAuthenticated(true);
            System.out.println("Authentication successful.");
        } else {
            AuthManager.clear();
            System.out.println("Invalid credentials.");
        }
    }
}