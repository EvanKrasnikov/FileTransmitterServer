package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private static Selector selector;
    private static ServerSocketChannel channel;
    private static SelectionKey key;
    static HashMap<SelectionKey, Session> clients = new HashMap<>();

    public static void main(String[] args) throws UnknownHostException {
        new Server(new InetSocketAddress(InetAddress.getLocalHost(), 1337));
    }

    public Server(InetSocketAddress address){
        try {
            selector = Selector.open();
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.bind(address);
            channel.register(selector,SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fail to initialize connection");
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> loop(), 0, 500, TimeUnit.MILLISECONDS);
    }

    private void loop(){
        try {
            selector.selectNow();

            for (SelectionKey key : selector.selectedKeys()) {
                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    SocketChannel acceptedChannel = channel.accept();

                    if (acceptedChannel == null) continue;

                    acceptedChannel.configureBlocking(false);
                    SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
                    clients.put(readKey, new Session(readKey, acceptedChannel));

                    System.out.println("New client " + acceptedChannel.getRemoteAddress());
                }

                if (key.isReadable()) {
                    Session session = clients.get(key);

                    if (session == null) continue;

                    session.receiveMessage();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fail to select key");
        }
    }
}
