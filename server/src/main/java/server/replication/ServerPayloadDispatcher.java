package server.replication;

import common.network.Request;
import common.network.Response;
import common.replication.ServerMode;
import common.replication.SyncMessage;
import server.connection.RequestHandler;
import server.managers.CollectionManager;

public class ServerPayloadDispatcher {

    private final RequestHandler requestHandler;
    private final ServerMode serverMode;
    private final CommandTypeClassifier classifier;
    private final SlaveSyncService slaveSyncService;
    private final ReplicationManager replicationManager;
    private final CollectionManager collectionManager;

    public ServerPayloadDispatcher(RequestHandler requestHandler,
                                   ServerMode serverMode,
                                   CommandTypeClassifier classifier,
                                   SlaveSyncService slaveSyncService,
                                   ReplicationManager replicationManager,
                                   CollectionManager collectionManager) {
        this.requestHandler = requestHandler;
        this.serverMode = serverMode;
        this.classifier = classifier;
        this.slaveSyncService = slaveSyncService;
        this.replicationManager = replicationManager;
        this.collectionManager = collectionManager;
    }

    public Response dispatch(Object payload) {
        if (payload == null) {
            return new Response(false, "Payload is null.");
        }

        if (payload instanceof SyncMessage syncMessage) {
            slaveSyncService.applySync(syncMessage);
            return new Response(true, "Slave synchronized successfully.");
        }

        if (!(payload instanceof Request request)) {
            return new Response(false, "Unsupported payload type: " + payload.getClass().getName());
        }

        if (request.getCommandType() == null) {
            return new Response(false, "Command type is null.");
        }

        if (serverMode == ServerMode.SLAVE && classifier.isWrite(request.getCommandType())) {
            return new Response(false, "This server is in SLAVE mode. Write commands are forbidden.");
        }

        Response response = requestHandler.handle(request);

        if (serverMode == ServerMode.MASTER
                && response.isSuccess()
                && classifier.isWrite(request.getCommandType())) {
            replicationManager.replicate(collectionManager.getCollection());
        }

        return response;
    }
}