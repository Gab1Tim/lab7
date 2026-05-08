package server;

import common.config.AppConfig;
import common.network.CommandType;
import common.replication.ServerMode;
import server.commands.*;
import server.connection.RequestHandler;
import server.connection.UdpServer;
import server.managers.CollectionManager;
import server.managers.CollectionPersistenceManager;
import server.managers.CommandManager;
import server.replication.CommandTypeClassifier;
import server.replication.ReplicationManager;
import server.replication.ServerPayloadDispatcher;
import server.replication.SlaveSyncService;

public class ServerMain {
    public static void main(String[] args) {
        String fileName = System.getenv("ORGANIZATION_FILE");
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalStateException("ORGANIZATION_FILE environment variable is not set");
        }

        AppConfig config = new AppConfig("config.properties");

        int port = config.getInt("server.port");
        int bufferSize = config.getInt("server.bufferSize");

        String modeValue = config.getString("server.mode").trim().toUpperCase();
        ServerMode serverMode = ServerMode.valueOf(modeValue);

        String slavesConfig = config.getString("server.slaves");
        ReplicationManager replicationManager = new ReplicationManager(serverMode, slavesConfig);

        CollectionPersistenceManager persistenceManager =
                new CollectionPersistenceManager(fileName);

        CollectionManager collectionManager =
                new CollectionManager(persistenceManager.loadCollection());

        CommandManager commandManager = new CommandManager();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                persistenceManager.saveSafely(collectionManager.getCollection());
                System.out.println("Collection saved. Server is shutting down.");
            } catch (Exception e) {
                System.out.println("Shutdown save error: " + e.getMessage());
            }
        }));

        commandManager.registerCommand(CommandType.INSERT, new InsertCommand(collectionManager, persistenceManager));
        commandManager.registerCommand(CommandType.SHOW, new ShowCommand(collectionManager));
        commandManager.registerCommand(CommandType.INFO, new InfoCommand(collectionManager));
        commandManager.registerCommand(CommandType.REMOVE_KEY, new RemoveKeyCommand(collectionManager, persistenceManager));
        commandManager.registerCommand(CommandType.CLEAR, new ClearCommand(collectionManager, persistenceManager));
        commandManager.registerCommand(CommandType.UPDATE, new UpdateCommand(collectionManager, persistenceManager));
        commandManager.registerCommand(CommandType.REMOVE_GREATER_KEY, new RemoveGreaterKeyCommand(collectionManager, persistenceManager));
        commandManager.registerCommand(CommandType.MIN_BY_NAME, new MinByNameCommand(collectionManager));
        commandManager.registerCommand(CommandType.FILTER_GREATER_THAN_TYPE, new FilterGreaterThanTypeCommand(collectionManager));
        commandManager.registerCommand(CommandType.PRINT_UNIQUE_ANNUAL_TURNOVER, new PrintUniqueAnnualTurnoverCommand(collectionManager));
        commandManager.registerCommand(CommandType.REMOVE_LOWER, new RemoveLowerCommand(collectionManager, persistenceManager));
        commandManager.registerCommand(CommandType.HELP, new HelpCommand(commandManager));

        CommandTypeClassifier classifier = new CommandTypeClassifier();
        SlaveSyncService slaveSyncService = new SlaveSyncService(collectionManager);

        RequestHandler requestHandler = new RequestHandler(commandManager);

        ServerPayloadDispatcher dispatcher = new ServerPayloadDispatcher(
                requestHandler,
                serverMode,
                classifier,
                slaveSyncService,
                replicationManager,
                collectionManager
        );

        UdpServer udpServer = new UdpServer(port, bufferSize, dispatcher);

        System.out.println("Server started on port " + port);
        System.out.println("Server mode: " + serverMode);

        udpServer.start();
    }
}