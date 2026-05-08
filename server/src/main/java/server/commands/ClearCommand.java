package server.commands;

import common.network.Request;
import server.managers.CollectionManager;
import server.managers.CollectionPersistenceManager;

public class ClearCommand implements Command {
    private final CollectionManager collectionManager;
    private final CollectionPersistenceManager persistenceManager;

    public ClearCommand(CollectionManager collectionManager,
                        CollectionPersistenceManager persistenceManager) {
        this.collectionManager = collectionManager;
        this.persistenceManager = persistenceManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clears the collection";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            persistenceManager.logClear();
            collectionManager.clear();
            return new CommandResult(true, "Collection cleared successfully.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}