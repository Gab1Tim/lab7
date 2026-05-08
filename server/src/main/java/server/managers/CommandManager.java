package server.managers;

import common.network.CommandType;
import common.network.Request;
import server.commands.Command;
import server.commands.CommandResult;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<CommandType, Command> commands = new HashMap<>();

    public void registerCommand(CommandType type, Command command) {
        commands.put(type, command);
    }

    public CommandResult execute(Request request) {
        if (request == null || request.getCommandType() == null) {
            return new CommandResult(false, "Request is null.");
        }

        Command command = commands.get(request.getCommandType());
        if (command == null) {
            return new CommandResult(false, "Unknown command: " + request.getCommandType());
        }

        return command.execute(request);
    }

    public String getAllCommandsInfo() {
        StringBuilder sb = new StringBuilder();
        for (Command command : commands.values()) {
            sb.append(command.getName())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        return sb.toString();
    }
}