package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;
import server.managers.CollectionPersistenceManager;

public class InsertCommand implements Command {
    private final CollectionManager collectionManager;
    private final CollectionPersistenceManager persistenceManager;

    public InsertCommand(CollectionManager collectionManager,
                         CollectionPersistenceManager persistenceManager) {
        this.collectionManager = collectionManager;
        this.persistenceManager = persistenceManager;
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

            if (key == null) return new CommandResult(false, "Key is required.");
            if (organization == null) return new CommandResult(false, "Organization is required.");

            collectionManager.insert(key, organization);
            Organization inserted = collectionManager.getCollection().get(key);
            persistenceManager.logInsert(key, inserted);

            return new CommandResult(true, "Organization added successfully.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}