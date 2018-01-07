package server;

import basehandler.BaseHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

class Server {
    private final int PORT = 8189;
    private final String HOST_ADRESS = "localhost";
    private static final int BUFFER_SIZE = 4096;
    private static Selector selector = null;


    Server() throws IOException{
        selector = Selector.open();

        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(HOST_ADRESS, PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> i = selectedKeys.iterator();

            while (i.hasNext()) {
                SelectionKey key = i.next();
                i.remove();

                if (key.isAcceptable()) {
                    processAcceptEvent(socketChannel);
                    continue;
                }

                 if (key.isReadable()) {
                    processReadEvent(socketChannel, key);
                    socketChannel.close();
                    return;
                }
            }
        }
    }

    private static void processAcceptEvent(ServerSocketChannel socketChannel) throws IOException{
            SocketChannel client = socketChannel.accept();
            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_READ);
    }

    private static void processReadEvent(ServerSocketChannel socketChannel, SelectionKey key) throws IOException{
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        client.read(buffer);
        String result = new String(buffer.array()).trim();
        String[] strings = result.split("\\s");
        ArrayDeque<String> queue = new ArrayDeque<>();

        for (String s: strings){
            queue.push(s);
        }

        BaseHandler authHandler = new BaseHandler(socketChannel,key);
        authHandler.parseMassage(queue);
    }
}
