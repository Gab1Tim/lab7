package server.managers;

import common.models.Address;
import common.models.Coordinates;
import common.models.Organization;
import common.models.OrganizationType;
import server.db.DatabaseManager;

import java.sql.*;
import java.util.Date;
import java.util.LinkedHashMap;

public class DatabaseCollectionManager {

    private final DatabaseManager databaseManager;

    public DatabaseCollectionManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public LinkedHashMap<Integer, Organization> loadCollection() {
        LinkedHashMap<Integer, Organization> collection = new LinkedHashMap<>();
        String sql = "SELECT id, key, name, coordinate_x, coordinate_y, creation_date, annual_turnover, type, zip_code, owner_id FROM organizations";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                Integer key = rs.getInt("key");
                String name = rs.getString("name");
                double coordinateX = rs.getDouble("coordinate_x");
                int coordinateY = rs.getInt("coordinate_y");
                Date creationDate = rs.getTimestamp("creation_date");
                int annualTurnover = rs.getInt("annual_turnover");
                String typeStr = rs.getString("type");
                String zipCode = rs.getString("zip_code");
                Long ownerId = rs.getLong("owner_id");

                Coordinates coordinates = new Coordinates(coordinateX, coordinateY);
                OrganizationType type = typeStr != null ? OrganizationType.valueOf(typeStr) : null;
                Address address = new Address(zipCode);

                Organization org = new Organization(
                        id,
                        name,
                        coordinates,
                        creationDate,
                        annualTurnover,
                        type,
                        address,
                        ownerId
                );

                collection.put(key, org);
            }

        } catch (SQLException e) {
            System.err.println("Error loading collection from database: " + e.getMessage());
        }

        return collection;
    }

    public Organization insert(Integer key, Organization org, Long ownerId) {
        String sql = "INSERT INTO organizations (key, name, coordinate_x, coordinate_y, creation_date, annual_turnover, type, zip_code, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, key);
            ps.setString(2, org.getName());
            ps.setDouble(3, org.getCoordinates().getX());
            ps.setInt(4, org.getCoordinates().getY());
            ps.setTimestamp(5, new Timestamp(org.getCreationDate().getTime()));
            ps.setInt(6, org.getAnnualTurnover());
            ps.setString(7, org.getType() != null ? org.getType().name() : null);
            ps.setString(8, org.getOfficialAddress().getZipCode());
            ps.setLong(9, ownerId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("id");
                Organization inserted = new Organization(
                        generatedId,
                        org.getName(),
                        org.getCoordinates(),
                        org.getCreationDate(),
                        org.getAnnualTurnover(),
                        org.getType(),
                        org.getOfficialAddress(),
                        ownerId
                );
                return inserted;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting organization: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Long id, Organization newOrg, Long ownerId) {
        String checkSql = "SELECT owner_id FROM organizations WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setLong(1, id);
            ResultSet rs = checkPs.executeQuery();
            if (!rs.next()) {
                return false;
            }
            long dbOwnerId = rs.getLong("owner_id");
            if (dbOwnerId != ownerId) {
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error checking ownership: " + e.getMessage());
            return false;
        }

        String updateSql = "UPDATE organizations SET name = ?, coordinate_x = ?, coordinate_y = ?, annual_turnover = ?, type = ?, zip_code = ? WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setString(1, newOrg.getName());
            ps.setDouble(2, newOrg.getCoordinates().getX());
            ps.setInt(3, newOrg.getCoordinates().getY());
            ps.setInt(4, newOrg.getAnnualTurnover());
            ps.setString(5, newOrg.getType() != null ? newOrg.getType().name() : null);
            ps.setString(6, newOrg.getOfficialAddress().getZipCode());
            ps.setLong(7, id);

            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating organization: " + e.getMessage());
            return false;
        }
    }

    public boolean remove(Integer key, Long ownerId) {
        String checkSql = "SELECT owner_id FROM organizations WHERE key = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setInt(1, key);
            ResultSet rs = checkPs.executeQuery();
            if (!rs.next()) {
                return false;
            }
            long dbOwnerId = rs.getLong("owner_id");
            if (dbOwnerId != ownerId) {
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error checking ownership: " + e.getMessage());
            return false;
        }

        String deleteSql = "DELETE FROM organizations WHERE key = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, key);
            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing organization: " + e.getMessage());
            return false;
        }
    }

    public int clear(Long ownerId) {
        String sql = "DELETE FROM organizations WHERE owner_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ownerId);
            int affected = ps.executeUpdate();
            return affected;

        } catch (SQLException e) {
            System.err.println("Error clearing user's organizations: " + e.getMessage());
            return 0;
        }
    }

    public boolean removeGreaterKey(Integer referenceKey, Long ownerId) {
        String sql = "DELETE FROM organizations WHERE key > ? AND owner_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, referenceKey);
            ps.setLong(2, ownerId);
            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing greater key: " + e.getMessage());
            return false;
        }
    }

    public int removeLower(Organization reference, Long ownerId) {
        String sql = "DELETE FROM organizations WHERE annual_turnover < ? AND owner_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reference.getAnnualTurnover());
            ps.setLong(2, ownerId);
            int affected = ps.executeUpdate();
            return affected;

        } catch (SQLException e) {
            System.err.println("Error removing lower: " + e.getMessage());
            return 0;
        }
    }
}