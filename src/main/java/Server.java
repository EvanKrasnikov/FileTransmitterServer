
import auth.ClientHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class Server {
    private final int PORT = 8189;
    private Vector<ClientHandler> clients;
    //private AuthService authService;
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;


    public Server(){
        ServerSocket server = null;
        Socket socket = null;
        authService = null;
        clients = new Vector<>();

        try{
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = socketChannel.socket();

            InetAddress hostAdress = InetAddress.getLocalHost();
            InetSocketAddress socketAddress = new InetSocketAddress(hostAdress, PORT);
            serverSocket.bind(socketAddress);

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
                        processReadEvent(key);
                    }
                    i.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

            System.out.println("Сервер запущен, ждем клиентов");


        }
    }
}
