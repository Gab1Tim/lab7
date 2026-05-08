package server.connection;

import common.network.Request;
import common.network.Response;
import server.managers.CommandManager;
import server.commands.CommandResult;

public class RequestHandler {

    private final CommandManager commandManager;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response handle(Request request) {
        CommandResult result = commandManager.execute(request);
        return new Response(result.isSuccess(), result.getMessage());
    }
}