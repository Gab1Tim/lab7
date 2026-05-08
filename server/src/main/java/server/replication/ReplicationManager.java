package server.replication;

import common.models.Organization;
import common.network.Serializer;
import common.replication.ServerMode;
import common.replication.SyncMessage;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReplicationManager {

    private final ServerMode serverMode;
    private final List<InetSocketAddress> slaveAddresses = new ArrayList<>();

    public ReplicationManager(ServerMode serverMode, String slavesConfig) {
        this.serverMode = serverMode;

        if (slavesConfig == null || slavesConfig.isBlank()) {
            return;
        }

        String[] entries = slavesConfig.split(",");
        for (String entry : entries) {
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) continue;

            String[] hostPort = trimmed.split(":");
            if (hostPort.length != 2) {
                throw new IllegalArgumentException("Invalid slave address: " + trimmed);
            }

            String host = hostPort[0].trim();
            int port = Integer.parseInt(hostPort[1].trim());

            slaveAddresses.add(new InetSocketAddress(host, port));
        }
    }

    public void replicate(LinkedHashMap<Integer, Organization> collection) {
        if (serverMode != ServerMode.MASTER) {
            return;
        }

        if (slaveAddresses.isEmpty()) {
            return;
        }

        LinkedHashMap<Integer, Organization> snapshot = new LinkedHashMap<>(collection);
        SyncMessage syncMessage = new SyncMessage(snapshot);

        try (DatagramChannel channel = DatagramChannel.open()) {
            byte[] bytes = Serializer.serialize(syncMessage);

            for (InetSocketAddress slaveAddress : slaveAddresses) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                channel.send(buffer, slaveAddress);
            }
        } catch (Exception e) {
            System.out.println("Replication error: " + e.getMessage());
        }
    }
}