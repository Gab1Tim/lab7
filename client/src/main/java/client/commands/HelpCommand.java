package client.commands;

import client.managers.ClientCommandManager;

public class HelpCommand implements Command {

    private final ClientCommandManager commandManager;

    public HelpCommand(ClientCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays a list of available client commands";
    }

    @Override
    public void execute(String[] args) {
        commandManager.showAllCommands();
    }
}