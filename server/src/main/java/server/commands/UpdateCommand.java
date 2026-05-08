package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;
import server.managers.CollectionPersistenceManager;

public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;
    private final CollectionPersistenceManager persistenceManager;

    public UpdateCommand(CollectionManager collectionManager,
                         CollectionPersistenceManager persistenceManager) {
        this.collectionManager = collectionManager;
        this.persistenceManager = persistenceManager;
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

            if (id == null) return new CommandResult(false, "Id is required.");
            if (organization == null) return new CommandResult(false, "Organization is required.");

            collectionManager.update(id, organization);

            Organization updated = collectionManager.getCollection().values().stream()
                    .filter(org -> org.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Updated organization not found"));

            persistenceManager.logUpdate(id, updated);

            return new CommandResult(true, "Organization updated successfully.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}