package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;
import server.managers.CollectionPersistenceManager;

public class RemoveLowerCommand implements Command {
    private final CollectionManager collectionManager;
    private final CollectionPersistenceManager persistenceManager;

    public RemoveLowerCommand(CollectionManager collectionManager,
                              CollectionPersistenceManager persistenceManager) {
        this.collectionManager = collectionManager;
        this.persistenceManager = persistenceManager;
    }

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getDescription() {
        return "Removes all elements lower than the given element";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            Organization reference = request.getOrganization();
            if (reference == null) return new CommandResult(false, "Reference organization is required.");

            persistenceManager.logRemoveLower(reference);
            int removed = collectionManager.removeLower(reference);

            return new CommandResult(true, removed + " elements removed.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}