package client.managers;

import client.commands.Command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ClientCommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final LinkedList<String> history = new LinkedList<>();
    private final int HISTORY_LIMIT = 12;

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public boolean hasCommand(String commandName) {
        return commands.containsKey(commandName);
    }

    public void executeCommand(String inputLine) {
        if (inputLine == null || inputLine.isEmpty()) return;

        String[] parts = inputLine.trim().split("\\s+", 2);
        String commandName = parts[0];
        String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        Command command = commands.get(commandName);

        if (command != null) {
            history.add(commandName);

            if (history.size() > HISTORY_LIMIT) {
                history.removeFirst();
            }

            command.execute(args);
        } else {
            System.out.println("Unknown client command: " + commandName);
        }
    }

    public void showAllCommands() {
        for (Command command : commands.values()) {
            System.out.println(command.getName() + " - " + command.getDescription());
        }
    }

    public void showHistory() {
        for (String cmd : history) {
            System.out.println(cmd);
        }
    }
}