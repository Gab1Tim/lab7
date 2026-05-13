package client.commands;

import client.auth.AuthManager;
import client.managers.InputManager;

public class RegisterCommand implements Command {
    private final java.util.function.BiFunction<String, String, Boolean> registerAction;

    public RegisterCommand(java.util.function.BiFunction<String, String, Boolean> registerAction) {
        this.registerAction = registerAction;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "Register a new user";
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
        boolean success = registerAction.apply(login, password);
        if (success) {
            AuthManager.setAuthenticated(true);
            System.out.println("Registration successful.");
        } else {
            AuthManager.clear();
            System.out.println("Registration failed.");
        }
    }
}