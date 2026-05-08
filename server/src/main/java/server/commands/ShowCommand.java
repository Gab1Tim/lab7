package server.commands;

import common.network.Request;
import server.managers.CollectionManager;

public class ShowCommand implements Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Shows all elements in the collection";
    }

    @Override
    public CommandResult execute(Request request) {
        return new CommandResult(true, collectionManager.getShowData());
    }
}