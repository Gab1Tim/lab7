package server.commands;

import common.network.Request;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

public class RemoveKeyCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseCollectionManager dbCollectionManager;

    public RemoveKeyCommand(CollectionManager collectionManager,
                            DatabaseCollectionManager dbCollectionManager) {
        this.collectionManager = collectionManager;
        this.dbCollectionManager = dbCollectionManager;
    }

    @Override
    public String getName() {
        return "remove_key";
    }

    @Override
    public String getDescription() {
        return "Removes an element by key";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            Integer key = request.getKey();
            Long userId = request.getUserId();

            if (key == null) return new CommandResult(false, "Key is required.");
            if (userId == null) return new CommandResult(false, "User not authenticated.");

            boolean success = dbCollectionManager.remove(key, userId);
            if (success) {
                collectionManager.remove(key);
                return new CommandResult(true, "Element with key " + key + " removed successfully.");
            } else {
                return new CommandResult(false, "Remove failed. Check key and ownership.");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}