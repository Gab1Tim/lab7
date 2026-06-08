package server.connection;

import common.network.Response;
import common.network.Serializer;
import server.replication.ServerPayloadDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class UdpServer {
    private final int port;
    private final int bufferSize;
    private final ServerPayloadDispatcher dispatcher;

    private final ExecutorService readPool = Executors.newCachedThreadPool();
    private final ExecutorService processPool = Executors.newCachedThreadPool();
    private final ExecutorService sendPool = Executors.newFixedThreadPool(10);

    private volatile boolean running = true;
    private DatagramChannel channel;
    private Selector selector;

    public UdpServer(int port, int bufferSize, ServerPayloadDispatcher dispatcher) {
        this.port = port;
        this.bufferSize = bufferSize;
        this.dispatcher = dispatcher;
    }

    public void start() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            System.out.println("UDP server is listening on port " + port);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(bufferSize);

            while (running) {

                if (selector.select(100) == 0) {
                    continue;
                }

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (!key.isValid() || !key.isReadable()) {
                        continue;
                    }

                    receiveBuffer.clear();
                    InetSocketAddress clientAddress =
                            (InetSocketAddress) channel.receive(receiveBuffer);

                    if (clientAddress == null) {
                        continue;
                    }

                    receiveBuffer.flip();
                    byte[] requestBytes = new byte[receiveBuffer.remaining()];
                    receiveBuffer.get(requestBytes);

                    InetSocketAddress finalClientAddress = clientAddress;

                    readPool.submit(() -> {
                        try {
                            Object payload = Serializer.deserialize(requestBytes);

                            Future<Response> futureResponse = processPool.submit(() -> {
                                Response response = dispatcher.dispatch(payload);
                                if (response == null) {
                                    response = new Response(false, "Dispatcher returned null response.");
                                }
                                return response;
                            });

                            sendPool.submit(() -> {
                                try {
                                    Response response = futureResponse.get();
                                    byte[] responseBytes = Serializer.serialize(response);
                                    ByteBuffer sendBuffer = ByteBuffer.wrap(responseBytes);
                                    channel.send(sendBuffer, finalClientAddress);
                                } catch (Exception e) {
                                    System.err.println("Failed to send response: " + e.getMessage());
                                }
                            });

                        } catch (Exception e) {
                            Response errorResponse = new Response(false, "Server error: " + e.getMessage());
                            sendPool.submit(() -> {
                                try {
                                    byte[] responseBytes = Serializer.serialize(errorResponse);
                                    ByteBuffer sendBuffer = ByteBuffer.wrap(responseBytes);
                                    channel.send(sendBuffer, finalClientAddress);
                                } catch (Exception ex) {
                                    System.err.println("Failed to send error response: " + ex.getMessage());
                                }
                            });
                        }
                    });
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not start UDP server on port " + port, e);
        } finally {
            stopPoolsAndChannels();
        }
    }

    public void stop() {
        running = false;
        if (selector != null) {
            selector.wakeup();
        }
    }

    private void stopPoolsAndChannels() {
        readPool.shutdownNow();
        processPool.shutdownNow();
        sendPool.shutdownNow();
        try { selector.close(); } catch (Exception ignored) {}
        try { channel.close(); } catch (Exception ignored) {}
    }
}