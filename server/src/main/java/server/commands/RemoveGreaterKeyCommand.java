package server.commands;

import common.network.Request;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

import java.sql.SQLException;

public class RemoveGreaterKeyCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseCollectionManager dbCollectionManager;

    public RemoveGreaterKeyCommand(CollectionManager collectionManager,
                                   DatabaseCollectionManager dbCollectionManager) {
        this.collectionManager = collectionManager;
        this.dbCollectionManager = dbCollectionManager;
    }

    @Override
    public String getName() {
        return "remove_greater_key";
    }

    @Override
    public String getDescription() {
        return "Removes all elements with key greater than the given key";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            Integer key = request.getKey();
            Long userId = request.getUserId();

            if (key == null) return new CommandResult(false, "Reference key is required.");
            if (userId == null) return new CommandResult(false, "User not authenticated.");

            boolean success = dbCollectionManager.removeGreaterKey(key, userId);
            if (success) {
                collectionManager.removeGreaterKey(key, userId);
                return new CommandResult(true, "Elements with greater key removed successfully.");
            } else {
                return new CommandResult(false, "No elements removed.");
            }
        } catch (SQLException e) {
            return new CommandResult(false, "Database error: " + e.getMessage());
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}