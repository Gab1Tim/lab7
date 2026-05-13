package server;

import common.config.AppConfig;
import common.network.CommandType;
import server.commands.*;
import server.connection.RequestHandler;
import server.connection.UdpServer;
import server.db.DatabaseManager;
import server.managers.*;
import server.replication.*;

public class ServerMain {
    public static void main(String[] args) {
        AppConfig config = new AppConfig("config.properties");

        String modeStr = config.getString("server.mode").toUpperCase();
        ServerMode serverMode = ServerMode.valueOf(modeStr);
        int port = config.getInt("server.port");
        int bufferSize = config.getInt("server.bufferSize");

        String dbUrl = config.getString("db.url");
        String dbUser = config.getString("db.user");
        String dbPassword = config.getString("db.password");
        String dbSchema = config.getString("db.schema");
        DatabaseManager dbManager = new DatabaseManager(dbUrl, dbUser, dbPassword, dbSchema);
        dbManager.initializeDatabase();

        DatabaseCollectionManager dbCollectionManager = new DatabaseCollectionManager(dbManager);
        UserManager userManager = new UserManager(dbManager);

        CollectionManager collectionManager = new CollectionManager(dbCollectionManager.loadCollection());

        CommandManager commandManager = new CommandManager();

        commandManager.registerCommand(CommandType.INSERT,
                new InsertCommand(collectionManager, dbCollectionManager));
        commandManager.registerCommand(CommandType.SHOW,
                new ShowCommand(collectionManager));
        commandManager.registerCommand(CommandType.INFO,
                new InfoCommand(collectionManager));
        commandManager.registerCommand(CommandType.REMOVE_KEY,
                new RemoveKeyCommand(collectionManager, dbCollectionManager));
        commandManager.registerCommand(CommandType.CLEAR,
                new ClearCommand(collectionManager, dbCollectionManager));
        commandManager.registerCommand(CommandType.UPDATE,
                new UpdateCommand(collectionManager, dbCollectionManager));
        commandManager.registerCommand(CommandType.REMOVE_GREATER_KEY,
                new RemoveGreaterKeyCommand(collectionManager, dbCollectionManager));
        commandManager.registerCommand(CommandType.MIN_BY_NAME,
                new MinByNameCommand(collectionManager));
        commandManager.registerCommand(CommandType.FILTER_GREATER_THAN_TYPE,
                new FilterGreaterThanTypeCommand(collectionManager));
        commandManager.registerCommand(CommandType.PRINT_UNIQUE_ANNUAL_TURNOVER,
                new PrintUniqueAnnualTurnoverCommand(collectionManager));
        commandManager.registerCommand(CommandType.REMOVE_LOWER,
                new RemoveLowerCommand(collectionManager, dbCollectionManager));
        commandManager.registerCommand(CommandType.HELP,
                new HelpCommand(commandManager));
        commandManager.registerCommand(CommandType.LOGIN,
                new LoginCommand(userManager));
        commandManager.registerCommand(CommandType.REGISTER,
                new RegisterCommand(userManager));

        CommandTypeClassifier classifier = new CommandTypeClassifier();
        RequestHandler requestHandler = new RequestHandler(commandManager);

        ReplicationNotifier notifier = new ReplicationNotifier(dbManager);

        ServerPayloadDispatcher dispatcher = new ServerPayloadDispatcher(
                requestHandler,
                serverMode,
                classifier,
                userManager,
                notifier
        );

        if (serverMode == ServerMode.SLAVE) {
            ReplicationListener listener = new ReplicationListener(
                    dbManager,
                    dbCollectionManager,
                    collectionManager
            );
            Thread listenerThread = new Thread(listener, "ReplicationListener");
            listenerThread.setDaemon(true);
            listenerThread.start();
            System.out.println("Replication listener started.");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server is shutting down.");
        }));

        UdpServer udpServer = new UdpServer(port, bufferSize, dispatcher);

        System.out.println("Server started on port " + port);
        System.out.println("Server mode: " + serverMode);

        udpServer.start();
    }
}