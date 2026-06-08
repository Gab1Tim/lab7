package server.auth;

import server.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class TokenManager {

    private final DatabaseManager databaseManager;
    private static final long TOKEN_VALIDITY_MS = 3600000;

    public TokenManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String generateToken(Long userId) throws SQLException {
        String token = UUID.randomUUID().toString();
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + TOKEN_VALIDITY_MS);

        String sql = "UPDATE users SET token = ?, token_expiry = ? WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, expiry);
            ps.setLong(3, userId);
            ps.executeUpdate();
            return token;
        }
    }

    public TokenInfo validateToken(String token) throws SQLException {
        String sql = "SELECT id, role FROM users WHERE token = ? AND token_expiry > NOW()";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("id");
                String roleStr = rs.getString("role");
                return new TokenInfo(userId, roleStr);
            }
        }
        return null;
    }
    public static class TokenInfo {
        private final long userId;
        private final String role;

        public TokenInfo(long userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        public long getUserId() { return userId; }
        public String getRole() { return role; }
    }
}