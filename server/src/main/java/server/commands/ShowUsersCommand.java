package server.commands;

import common.network.Request;
import server.db.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShowUsersCommand implements Command {

    private final DatabaseManager databaseManager;

    public ShowUsersCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public String getName() {
        return "show_users";
    }

    @Override
    public String getDescription() {
        return "Shows list of all users and their roles";
    }

    @Override
    public CommandResult execute(Request request) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT login, role FROM users ORDER BY login";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sb.append(rs.getString("login"))
                        .append(" - ")
                        .append(rs.getString("role"))
                        .append("\n");
            }
        } catch (SQLException e) {
            return new CommandResult(false, "Database error: " + e.getMessage());
        }

        if (sb.isEmpty()) {
            return new CommandResult(true, "No users found.");
        }
        return new CommandResult(true, sb.toString().trim());
    }
}