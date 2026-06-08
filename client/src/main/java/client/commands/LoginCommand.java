package client.commands;

import client.auth.AuthManager;
import client.managers.InputManager;

public class LoginCommand implements Command {
    private final java.util.function.Function<String[], common.network.Response> authAction;

    public LoginCommand(java.util.function.Function<String[], common.network.Response> authAction) {
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
        common.network.Response response = authAction.apply(new String[]{login, password});
        if (response.isSuccess()) {
            if (response.getToken() != null && !response.getToken().isEmpty()) {
                AuthManager.setToken(response.getToken());
                AuthManager.setAuthenticated(true);
                System.out.println("Authentication successful.");
            } else {
                AuthManager.clear();
                System.out.println("Login failed. No token received.");
            }
        } else {
            AuthManager.clear();
            System.out.println("Invalid credentials.");
        }
    }
}