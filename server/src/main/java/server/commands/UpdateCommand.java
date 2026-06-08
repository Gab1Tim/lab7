package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

import java.sql.SQLException;

public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseCollectionManager dbCollectionManager;

    public UpdateCommand(CollectionManager collectionManager,
                         DatabaseCollectionManager dbCollectionManager) {
        this.collectionManager = collectionManager;
        this.dbCollectionManager = dbCollectionManager;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "Updates an existing Organization by its id";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            Long id = request.getId();
            Organization organization = request.getOrganization();
            Long userId = request.getUserId();

            if (id == null) return new CommandResult(false, "Id is required.");
            if (organization == null) return new CommandResult(false, "Organization is required.");
            if (userId == null) return new CommandResult(false, "User not authenticated.");

            boolean success = dbCollectionManager.update(id, organization, userId);
            if (success) {
                collectionManager.update(id, organization);
                return new CommandResult(true, "Organization updated successfully.");
            } else {
                return new CommandResult(false, "Update failed. Check id and ownership.");
            }
        } catch (SQLException e) {
            return new CommandResult(false, "Database error: " + e.getMessage());
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}