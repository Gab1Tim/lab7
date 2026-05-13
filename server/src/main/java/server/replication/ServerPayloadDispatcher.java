package server.replication;

import common.network.CommandType;
import common.network.Request;
import common.network.Response;
import server.connection.RequestHandler;
import server.managers.UserManager;

public class ServerPayloadDispatcher {

    private final RequestHandler requestHandler;
    private final ServerMode serverMode;
    private final CommandTypeClassifier classifier;
    private final UserManager userManager;
    private final ReplicationNotifier notifier;

    public ServerPayloadDispatcher(RequestHandler requestHandler,
                                   ServerMode serverMode,
                                   CommandTypeClassifier classifier,
                                   UserManager userManager,
                                   ReplicationNotifier notifier) {
        this.requestHandler = requestHandler;
        this.serverMode = serverMode;
        this.classifier = classifier;
        this.userManager = userManager;
        this.notifier = notifier;
    }

    public Response dispatch(Object payload) {
        if (payload == null) {
            return new Response(false, "Payload is null.");
        }

        if (!(payload instanceof Request request)) {
            return new Response(false, "Unsupported payload type.");
        }

        CommandType commandType = request.getCommandType();
        if (commandType == null) {
            return new Response(false, "Command type is null.");
        }

        if (commandType != CommandType.LOGIN && commandType != CommandType.REGISTER) {
            String login = request.getLogin();
            String password = request.getPassword();
            if (login == null || password == null) {
                return new Response(false, "Authentication required.");
            }
            Long userId = userManager.authenticate(login, password);
            if (userId == null) {
                return new Response(false, "Invalid login or password.");
            }
            request.setUserId(userId);
        }

        if (serverMode == ServerMode.SLAVE && classifier.isWrite(commandType)) {
            return new Response(false, "This server is in SLAVE mode. Write commands are forbidden.");
        }

        Response response = requestHandler.handle(request);

        if (serverMode == ServerMode.MASTER
                && response.isSuccess()
                && classifier.isWrite(commandType)) {
            notifier.notifyCollectionChanged();
        }

        return response;
    }
}