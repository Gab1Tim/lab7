package server.commands;

import common.network.Request;
import server.managers.UserManager;

import java.sql.SQLException;

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

        try {
            String token = userManager.authenticate(login, password);
            if (token != null) {
                return new CommandResult(true, "Authentication successful", token);
            } else {
                return new CommandResult(false, "Invalid login or password");
            }
        } catch (SQLException e) {
            return new CommandResult(false, "Database error: " + e.getMessage());
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}