package client.commands;

import client.managers.ClientCommandManager;

public class HistoryCommand implements Command {

    private final ClientCommandManager commandManager;

    public HistoryCommand(ClientCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public String getDescription() {
        return "Shows last 12 client commands";
    }

    @Override
    public void execute(String[] args) {
        commandManager.showHistory();
    }
}