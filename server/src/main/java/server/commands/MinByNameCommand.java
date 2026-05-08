package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;

public class MinByNameCommand implements Command {
    private final CollectionManager collectionManager;

    public MinByNameCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "min_by_name";
    }

    @Override
    public String getDescription() {
        return "Shows the organization with the minimum name";
    }

    @Override
    public CommandResult execute(Request request) {
        Organization org = collectionManager.getMinByName();
        if (org == null) {
            return new CommandResult(true, "Collection is empty.");
        }
        return new CommandResult(true, org.toString());
    }
}