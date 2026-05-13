package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

public class RemoveLowerCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseCollectionManager dbCollectionManager;

    public RemoveLowerCommand(CollectionManager collectionManager,
                              DatabaseCollectionManager dbCollectionManager) {
        this.collectionManager = collectionManager;
        this.dbCollectionManager = dbCollectionManager;
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
            Long userId = request.getUserId();

            if (reference == null) return new CommandResult(false, "Reference organization is required.");
            if (userId == null) return new CommandResult(false, "User not authenticated.");

            int removed = dbCollectionManager.removeLower(reference, userId);
            collectionManager.removeLower(reference, userId);
            return new CommandResult(true, removed + " elements removed.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}