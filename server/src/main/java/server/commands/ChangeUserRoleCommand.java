package server.commands;

import common.auth.Role;
import common.network.Request;
import server.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangeUserRoleCommand implements Command {

    private final DatabaseManager databaseManager;

    public ChangeUserRoleCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public String getName() {
        return "change_user_role";
    }

    @Override
    public String getDescription() {
        return "Changes role of a user. Usage: change_user_role <login> <new_role>";
    }

    @Override
    public CommandResult execute(Request request) {
        String targetLogin = request.getLogin();
        String newRoleStr = request.getPassword();

        if (targetLogin == null || newRoleStr == null) {
            return new CommandResult(false, "Usage: change_user_role <login> <new_role>");
        }

        Role newRole;
        try {
            newRole = Role.valueOf(newRoleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CommandResult(false, "Invalid role. Valid roles: ADMIN, USER_TEAMLEAD, USER_JUNIOR");
        }

        Long currentUserId = request.getUserId();
        if (currentUserId == null) {
            return new CommandResult(false, "User not authenticated.");
        }

        try (Connection conn = databaseManager.getConnection()) {

            String getCurrentLoginSql = "SELECT login FROM users WHERE id = ?";
            PreparedStatement currentPs = conn.prepareStatement(getCurrentLoginSql);
            currentPs.setLong(1, currentUserId);
            ResultSet currentRs = currentPs.executeQuery();
            if (currentRs.next() && currentRs.getString("login").equals(targetLogin)) {
                return new CommandResult(false, "You cannot change your own role.");
            }

            String checkSql = "SELECT id FROM users WHERE login = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, targetLogin);
            ResultSet checkRs = checkPs.executeQuery();
            if (!checkRs.next()) {
                return new CommandResult(false, "User not found: " + targetLogin);
            }

            String updateSql = "UPDATE users SET role = ?, token = NULL, token_expiry = NULL WHERE login = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setString(1, newRole.name());
            updatePs.setString(2, targetLogin);
            updatePs.executeUpdate();

            return new CommandResult(true, "Role updated for " + targetLogin + " to " + newRole.name());

        } catch (SQLException e) {
            return new CommandResult(false, "Database error: " + e.getMessage());
        }
    }
}