package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

public class InsertCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseCollectionManager dbCollectionManager;

    public InsertCommand(CollectionManager collectionManager,
                         DatabaseCollectionManager dbCollectionManager) {
        this.collectionManager = collectionManager;
        this.dbCollectionManager = dbCollectionManager;
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getDescription() {
        return "Inserts a new Organization with a unique key";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            Integer key = request.getKey();
            Organization organization = request.getOrganization();
            Long userId = request.getUserId();

            if (key == null) return new CommandResult(false, "Key is required.");
            if (organization == null) return new CommandResult(false, "Organization is required.");
            if (userId == null) return new CommandResult(false, "User not authenticated.");

            Organization inserted = dbCollectionManager.insert(key, organization, userId);
            if (inserted != null) {
                collectionManager.insert(key, inserted);
                return new CommandResult(true, "Organization added successfully.");
            } else {
                return new CommandResult(false, "Failed to insert organization.");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}