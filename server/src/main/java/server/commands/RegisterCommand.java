package server.commands;

import common.network.Request;
import server.managers.UserManager;

import java.sql.SQLException;

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

        try {
            String token = userManager.registerAndGetToken(login, password);
            if (token != null) {
                return new CommandResult(true, "Registration successful", token);
            } else {
                return new CommandResult(false, "Registration failed.");
            }
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg.contains("duplicate key") && msg.contains("users_login_key")) {
                return new CommandResult(false, "Login already exists. Please choose a different login.");
            }
            return new CommandResult(false, "Database error: " + msg);
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}