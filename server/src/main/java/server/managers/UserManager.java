package server.managers;

import server.auth.TokenManager;
import server.db.DatabaseManager;
import server.util.PasswordHasher;

import java.sql.*;

public class UserManager {

    private final DatabaseManager databaseManager;
    private final TokenManager tokenManager;

    public UserManager(DatabaseManager databaseManager, TokenManager tokenManager) {
        this.databaseManager = databaseManager;
        this.tokenManager = tokenManager;
    }

    public String registerAndGetToken(String login, String password) throws SQLException {
        String hashedPassword = PasswordHasher.hash(password);
        String sql = "INSERT INTO users (login, password, role) VALUES (?, ?, 'USER_JUNIOR') RETURNING id";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("id");
                return tokenManager.generateToken(userId);
            }
        }
        return null;
    }

    public String authenticate(String login, String password) throws SQLException {
        String hashedPassword = PasswordHasher.hash(password);
        String sql = "SELECT id FROM users WHERE login = ? AND password = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("id");
                return tokenManager.generateToken(userId);
            }
        }
        return null;
    }
}