package server.connection;

import common.network.Request;
import common.network.Response;
import server.commands.CommandResult;
import server.managers.CommandManager;

public class RequestHandler {
    private final CommandManager commandManager;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response handle(Request request) {
        CommandResult result = commandManager.execute(request);
        Response response = new Response(result.isSuccess(), result.getMessage());
        if (result.getToken() != null) {
            response.setToken(result.getToken());
        }
        return response;
    }
}