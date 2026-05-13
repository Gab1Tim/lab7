package server.commands;

import common.network.Request;
import server.managers.UserManager;

public class RegisterCommand implements Command {

    private final UserManager userManager;

    public RegisterCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "Register a new user with login and password";
    }

    @Override
    public CommandResult execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || password == null) {
            return new CommandResult(false, "Login and password are required");
        }

        Long userId = userManager.register(login, password);
        if (userId != null) {
            return new CommandResult(true, "Registration successful. User ID: " + userId);
        } else {
            return new CommandResult(false, "Registration failed. Login may already exist");
        }
    }
}