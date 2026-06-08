package server.replication;

import common.auth.Role;
import common.network.CommandType;
import common.network.Request;
import common.network.Response;
import server.auth.PermissionManager;
import server.auth.TokenManager;
import server.connection.RequestHandler;

public class ServerPayloadDispatcher {

    private final RequestHandler requestHandler;
    private final ServerMode serverMode;
    private final CommandTypeClassifier classifier;
    private final TokenManager tokenManager;
    private final PermissionManager permissionManager;
    private final ReplicationNotifier notifier;

    public ServerPayloadDispatcher(RequestHandler requestHandler,
                                   ServerMode serverMode,
                                   CommandTypeClassifier classifier,
                                   TokenManager tokenManager,
                                   PermissionManager permissionManager,
                                   ReplicationNotifier notifier) {
        this.requestHandler = requestHandler;
        this.serverMode = serverMode;
        this.classifier = classifier;
        this.tokenManager = tokenManager;
        this.permissionManager = permissionManager;
        this.notifier = notifier;
    }

    public Response dispatch(Object payload) {
        if (payload == null) return new Response(false, "Payload is null.");
        if (!(payload instanceof Request request)) return new Response(false, "Unsupported payload type.");

        CommandType commandType = request.getCommandType();
        if (commandType == null) return new Response(false, "Command type is null.");


        if (commandType != CommandType.LOGIN && commandType != CommandType.REGISTER) {
            String token = request.getToken();
            if (token == null || token.isEmpty()) {
                return new Response(false, "Authentication required.");
            }

            TokenManager.TokenInfo tokenInfo;
            try {
                tokenInfo = tokenManager.validateToken(token);
            } catch (Exception e) {
                return new Response(false, "Token validation error: " + e.getMessage());
            }
            if (tokenInfo == null) {
                return new Response(false, "Invalid or expired token. Please login again.");
            }

            request.setUserId(tokenInfo.getUserId());

            Role userRole;
            try {
                userRole = Role.valueOf(tokenInfo.getRole());
            } catch (IllegalArgumentException e) {
                return new Response(false, "Unknown role: " + tokenInfo.getRole());
            }

            if (!permissionManager.hasPermission(userRole, commandType)) {
                return new Response(false, "Access denied. Insufficient permissions.");
            }
        }


        if (serverMode == ServerMode.SLAVE && classifier.isWrite(commandType)) {
            return new Response(false, "This server is in SLAVE mode. Write commands are forbidden.");
        }

        Response response = requestHandler.handle(request);


        if (serverMode == ServerMode.MASTER && response.isSuccess() && classifier.isWrite(commandType)) {
            notifier.notifyCollectionChanged();
        }

        return response;
    }
}