package server.commands;

import common.network.Request;
import server.managers.UserManager;

public class LoginCommand implements Command {

    private final UserManager userManager;

    public LoginCommand(UserManager userManager) {
        this.userManager = userManager;
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
    public CommandResult execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || password == null) {
            return new CommandResult(false, "Login and password are required");
        }

        Long userId = userManager.authenticate(login, password);
        if (userId != null) {
            return new CommandResult(true, "Authentication successful. User ID: " + userId);
        } else {
            return new CommandResult(false, "Invalid login or password");
        }
    }
}