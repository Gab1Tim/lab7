package server.connection;

import common.network.Response;
import common.network.Serializer;
import server.replication.ServerPayloadDispatcher;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpServer {
    private final int port;
    private final int bufferSize;
    private final ServerPayloadDispatcher dispatcher;

    public UdpServer(int port, int bufferSize, ServerPayloadDispatcher dispatcher) {
        this.port = port;
        this.bufferSize = bufferSize;
        this.dispatcher = dispatcher;
    }

    public void start() {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));

            System.out.println("UDP server is listening on port " + port);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(bufferSize);

            while (true) {
                receiveBuffer.clear();

                InetSocketAddress clientAddress =
                        (InetSocketAddress) channel.receive(receiveBuffer);

                if (clientAddress == null) {
                    Thread.sleep(10);
                    continue;
                }

                Response response;

                try {
                    receiveBuffer.flip();
                    byte[] requestBytes = new byte[receiveBuffer.remaining()];
                    receiveBuffer.get(requestBytes);

                    Object payload = Serializer.deserialize(requestBytes);
                    response = dispatcher.dispatch(payload);

                    if (response == null) {
                        response = new Response(false, "Dispatcher returned null response.");
                    }
                } catch (Exception e) {
                    response = new Response(false, "Server error: " + e.getMessage());
                }

                try {
                    byte[] responseBytes = Serializer.serialize(response);
                    ByteBuffer sendBuffer = ByteBuffer.wrap(responseBytes);
                    channel.send(sendBuffer, clientAddress);
                } catch (Exception e) {
                    System.err.println("Failed to send response: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not start UDP server on port " + port, e);
        }
    }
}