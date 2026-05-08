package server.commands;

import common.network.Request;
import server.managers.CollectionManager;

public class InfoCommand implements Command {
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Shows information about the collection";
    }

    @Override
    public CommandResult execute(Request request) {
        return new CommandResult(true, collectionManager.getInfoData());
    }
}