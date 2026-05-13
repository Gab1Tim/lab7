package server.replication;

import common.models.Organization;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import server.db.DatabaseManager;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

public class ReplicationListener implements Runnable {

    private final DatabaseManager databaseManager;
    private final DatabaseCollectionManager dbCollectionManager;
    private final CollectionManager collectionManager;
    private volatile boolean running = true;

    public ReplicationListener(DatabaseManager databaseManager,
                               DatabaseCollectionManager dbCollectionManager,
                               CollectionManager collectionManager) {
        this.databaseManager = databaseManager;
        this.dbCollectionManager = dbCollectionManager;
        this.collectionManager = collectionManager;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try (Connection conn = databaseManager.getConnection()) {
            PGConnection pgConn = conn.unwrap(PGConnection.class);
            Statement stmt = conn.createStatement();
            stmt.execute("LISTEN collection_changed");
            stmt.close();

            System.out.println("Slave is listening for replication notifications...");

            while (running) {
                PGNotification[] notifications = pgConn.getNotifications(500);
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        if ("collection_changed".equals(notification.getName())) {
                            System.out.println("Replication notification received. Reloading collection...");
                            LinkedHashMap<Integer, Organization> newCollection =
                                    dbCollectionManager.loadCollection();
                            collectionManager.replaceAll(newCollection);
                            System.out.println("Collection synchronized.");
                        }
                    }
                }
                Thread.sleep(100);
            }
        } catch (SQLException | InterruptedException e) {
            System.err.println("ReplicationListener error: " + e.getMessage());
        }
    }
}