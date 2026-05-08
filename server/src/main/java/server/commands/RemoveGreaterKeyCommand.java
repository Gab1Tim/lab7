package server.commands;

import common.network.Request;
import server.managers.CollectionManager;
import server.managers.CollectionPersistenceManager;

public class RemoveGreaterKeyCommand implements Command {
    private final CollectionManager collectionManager;
    private final CollectionPersistenceManager persistenceManager;

    public RemoveGreaterKeyCommand(CollectionManager collectionManager,
                                   CollectionPersistenceManager persistenceManager) {
        this.collectionManager = collectionManager;
        this.persistenceManager = persistenceManager;
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
            if (key == null) return new CommandResult(false, "Reference key is required.");

            persistenceManager.logRemoveGreaterKey(key);
            collectionManager.removeGreaterKey(key);

            return new CommandResult(true, "Elements with greater key removed successfully.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}