package client;
import client.connection.Client;
import common.config.AppConfig;

public class ClientMain {
    public static void main(String[] args) {
        AppConfig config = new AppConfig("config.properties");
        Client client = new Client(config);
        client.run();
    }
}