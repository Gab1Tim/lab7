package server.replication;

import server.db.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ReplicationNotifier {

    private final DatabaseManager databaseManager;

    public ReplicationNotifier(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void notifyCollectionChanged() {
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("NOTIFY collection_changed");
        } catch (SQLException e) {
            System.err.println("Failed to send NOTIFY: " + e.getMessage());
        }
    }
}