package server.commands;

import common.network.Request;
import server.managers.CollectionManager;
import server.managers.CollectionPersistenceManager;

public class RemoveKeyCommand implements Command {
    private final CollectionManager collectionManager;
    private final CollectionPersistenceManager persistenceManager;

    public RemoveKeyCommand(CollectionManager collectionManager,
                            CollectionPersistenceManager persistenceManager) {
        this.collectionManager = collectionManager;
        this.persistenceManager = persistenceManager;
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
            if (key == null) return new CommandResult(false, "Key is required.");

            persistenceManager.logRemove(key);
            collectionManager.remove(key);

            return new CommandResult(true, "Element with key " + key + " removed successfully.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}