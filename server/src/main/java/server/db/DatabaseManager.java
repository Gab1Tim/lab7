package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;
    private final String schema;

    public DatabaseManager(String url, String user, String password, String schema) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.schema = schema;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found", e);
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setSchema(schema);
        return conn;
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET SEARCH_PATH TO " + schema);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id       BIGSERIAL PRIMARY KEY,
                            login    VARCHAR(50) UNIQUE NOT NULL,
                            password TEXT NOT NULL
                        )
                    """);

            stmt.execute("CREATE SEQUENCE IF NOT EXISTS org_id_seq START 1");

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS organizations (
                            id              BIGINT PRIMARY KEY DEFAULT nextval('org_id_seq'),
                            key             INTEGER NOT NULL UNIQUE,
                            name            TEXT NOT NULL,
                            coordinate_x    DOUBLE PRECISION NOT NULL,
                            coordinate_y    INTEGER NOT NULL,
                            creation_date   TIMESTAMP NOT NULL DEFAULT NOW(),
                            annual_turnover INTEGER NOT NULL CHECK (annual_turnover > 0),
                            type            TEXT,
                            zip_code        VARCHAR(20),
                            owner_id        BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
                        )
                    """);

            System.out.println("Database initialized successfully in schema " + schema);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}