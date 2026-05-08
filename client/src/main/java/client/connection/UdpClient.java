package client.connection;

import common.network.Request;
import common.network.Response;
import common.network.Serializer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpClient {
    private final String host;
    private final int port;
    private final int bufferSize;
    private final int timeoutMillis;

    public UdpClient(String host, int port, int bufferSize, int timeoutMillis) {
        this.host = host;
        this.port = port;
        this.bufferSize = bufferSize;
        this.timeoutMillis = timeoutMillis;
    }

    public Response sendAndReceive(Request request) {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            InetSocketAddress serverAddress = new InetSocketAddress(host, port);

            byte[] requestBytes = Serializer.serialize(request);
            channel.send(ByteBuffer.wrap(requestBytes), serverAddress);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(bufferSize);
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < timeoutMillis) {
                receiveBuffer.clear();
                InetSocketAddress sender = (InetSocketAddress) channel.receive(receiveBuffer);

                if (sender != null) {
                    receiveBuffer.flip();
                    byte[] responseBytes = new byte[receiveBuffer.remaining()];
                    receiveBuffer.get(responseBytes);
                    return (Response) Serializer.deserialize(responseBytes);
                }

                Thread.sleep(10);
            }

            return new Response(false, "Server did not respond in time.");
        } catch (Exception e) {
            return new Response(false, "Client error: " + e.getMessage());
        }
    }
}