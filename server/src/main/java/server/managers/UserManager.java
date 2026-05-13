package server.managers;

import server.db.DatabaseManager;
import server.util.PasswordHasher;

import java.sql.*;

public class UserManager {

    private final DatabaseManager databaseManager;

    public UserManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Long register(String login, String password) {
        String hashedPassword = PasswordHasher.hash(password);
        String sql = "INSERT INTO users (login, password) VALUES (?, ?) RETURNING id";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
        return null;
    }

    public Long authenticate(String login, String password) {
        String hashedPassword = PasswordHasher.hash(password);
        String sql = "SELECT id FROM users WHERE login = ? AND password = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }
}