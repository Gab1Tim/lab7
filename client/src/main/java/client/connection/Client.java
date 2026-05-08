package client.connection;

import client.commands.ExecuteScriptCommand;
import client.commands.ExitCommand;
import client.commands.HistoryCommand;
import client.managers.ClientCommandManager;
import client.managers.InputManager;
import common.config.AppConfig;
import common.network.Request;
import common.network.Response;

public class Client {
    private final ClientCommandManager clientCommandManager;
    private final UdpClient udpClient;

    public Client(AppConfig config) {
        this.clientCommandManager = new ClientCommandManager();

        String host = config.getString("client.host");
        int port = config.getInt("client.port");
        int bufferSize = config.getInt("client.bufferSize");
        int timeoutMillis = config.getInt("client.timeoutMillis");

        this.udpClient = new UdpClient(host, port, bufferSize, timeoutMillis);
        registerLocalCommands();
    }

    private void registerLocalCommands() {
        clientCommandManager.registerCommand(new HistoryCommand(clientCommandManager));
        clientCommandManager.registerCommand(new ExecuteScriptCommand(this));
        clientCommandManager.registerCommand(new ExitCommand());
    }

    public void run() {
        System.out.println("Welcome! Type commands:");
        while (true) {
            String inputLine = InputManager.readLine("> ");
            if (inputLine == null) break;
            processInputLine(inputLine);
        }
    }

    public void processInputLine(String inputLine) {
        if (inputLine == null) return;
        inputLine = inputLine.trim();
        if (inputLine.isEmpty()) return;

        String commandName = inputLine.split("\\s+", 2)[0];

        if (commandName.equals("help")) {
            showHelp();
            return;
        }

        if (clientCommandManager.hasCommand(commandName)) {
            clientCommandManager.executeCommand(inputLine);
        } else {
            sendCommandToServer(commandName);
        }
    }

    private void sendCommandToServer(String commandName) {
        Request request = RequestFactory.buildRequest(commandName);
        if (request == null) {
            System.out.println("Unknown command or invalid arguments.");
            return;
        }

        Response response = udpClient.sendAndReceive(request);
        System.out.println(response.getMessage());
    }

    private void showHelp() {
        System.out.println("Local commands:");
        System.out.println("help - Displays help");
        System.out.println("history - Displays last 12 commands");
        System.out.println("execute_script filename - Executes commands from file");
        System.out.println("exit - Exits the program");
        System.out.println("Server commands:");
        sendCommandToServer("help");
    }
}