import auth.ClientHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class Server {
    private final int PORT = 8189;
    private final String HOST_ADRESS = "localhost";
    private Vector<ClientHandler> clients;
    //private AuthService authService;
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;


    public Server() throws IOException{
        //authService = null;
        clients = new Vector<>();
        selector = Selector.open();

        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress(HOST_ADRESS, PORT);

        socketChannel.bind(socketAddress);
        socketChannel.configureBlocking(false);

        int ops = socketChannel.validOps();
        socketChannel.register(selector, ops, null);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> i = selectedKeys.iterator();

            while (i.hasNext()) {
                SelectionKey key = i.next();

                if (key.isAcceptable()) {
                    processAcceptEvent(socketChannel, key);
                } else if (key.isReadable()) {
                    processReadEvent(socketChannel, key);
                }

                i.remove();
            }
        }
    }

    private static void processAcceptEvent(ServerSocketChannel socketChannel, SelectionKey key) throws IOException{
            SocketChannel client = socketChannel.accept();
            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_WRITE);
    }

    private static void processReadEvent(ServerSocketChannel socketChannel, SelectionKey key) throws IOException{
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        client.read(buffer);
        String result = new String(buffer.array()).trim();

        ClientHandler clientHandler = new ClientHandler(socketChannel,key);
        clientHandler.parseMassage(result);
    }
}
