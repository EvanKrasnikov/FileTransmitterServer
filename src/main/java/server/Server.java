package server;

import basehandler.BaseHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

class Server {
    private final int PORT = 8189;
    private final String HOST_ADRESS = "localhost";
    private static final int BUFFER_SIZE = 4096;
   // private static Selector selector = null;

    private Selector selector;
    private ServerSocketChannel server;
    private SocketChannel client;
    private final Map<SelectionKey, ByteBuffer> readBuffers = new HashMap<SelectionKey, ByteBuffer>();
    private State state;
    private final MessageLength messageLength = new TwoByteMessageLength();

    Server(){
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(HOST_ADRESS, PORT));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();

                    if (key.isConnectable()) {
                        ((SocketChannel)key.channel()).finishConnect();
                    }

                    if (key.isAcceptable()) {
                        client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector,SelectionKey.OP_READ);
                    }

                     if (key.isReadable()) {
                         for (ByteBuffer message: readIncomingMessage(key)) {
                             messageReceived(message, key);
                         }
                     }

                    if (key.isWritable()){

                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("Server failure: " + e.getMessage());
        } finally {
            try {
                selector.close();
                client.close();
            } catch (Throwable e) {
                System.err.println("Can't close channels: " + e.getMessage());
            }
        }
    }

    private List<ByteBuffer> readIncomingMessage(SelectionKey key) throws IOException {
        ByteBuffer readBuffer = readBuffers.get(key);
        if (readBuffer == null) {
            readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            readBuffers.put(key, readBuffer);
        }
        if (((ReadableByteChannel)key.channel()).read(readBuffer) == -1) {
            throw new IOException("Read on closed key");
        }

        readBuffer.flip();
        List<ByteBuffer> result = new ArrayList<ByteBuffer>();

        ByteBuffer msg = readMessage(key, readBuffer);
        while (msg != null) {
            result.add(msg);
            msg = readMessage(key, readBuffer);
        }

        return result;
    }

    private ByteBuffer readMessage(SelectionKey key, ByteBuffer readBuffer) {
        int bytesToRead;
        if (readBuffer.remaining() > messageLength.byteLength()) { // must have at least enough bytes to read the size of the message
            byte[] lengthBytes = new byte[messageLength.byteLength()];
            readBuffer.get(lengthBytes);
            bytesToRead = (int)messageLength.bytesToLength(lengthBytes);
            if ((readBuffer.limit() - readBuffer.position()) < bytesToRead) {
                // Not enough data - prepare for writing again
                if (readBuffer.limit() == readBuffer.capacity()) {
                    // message may be longer than buffer => resize buffer to message size
                    int oldCapacity = readBuffer.capacity();
                    ByteBuffer tmp = ByteBuffer.allocate(bytesToRead + messageLength.byteLength());
                    readBuffer.position(0);
                    tmp.put(readBuffer);
                    readBuffer = tmp;
                    readBuffer.position(oldCapacity);
                    readBuffer.limit(readBuffer.capacity());
                    readBuffers.put(key, readBuffer);
                    return null;
                } else {
                    // rest for writing
                    readBuffer.position(readBuffer.limit());
                    readBuffer.limit(readBuffer.capacity());
                    return null;
                }
            }
        } else {
            // Not enough data - prepare for writing again
            readBuffer.position(readBuffer.limit());
            readBuffer.limit(readBuffer.capacity());
            return null;
        }
        byte[] resultMessage = new byte[bytesToRead];
        readBuffer.get(resultMessage, 0, bytesToRead);
        // remove read message from buffer
        int remaining = readBuffer.remaining();
        readBuffer.limit(readBuffer.capacity());
        readBuffer.compact();
        readBuffer.position(0);
        readBuffer.limit(remaining);
        return ByteBuffer.wrap(resultMessage);
    }

    public void write(SelectionKey channelKey, byte[] buffer)  {
        short len = (short)buffer.length;
        byte[] lengthBytes = messageLength.lengthToBytes(len);
        // copying into byte buffer is actually faster than writing to channel twice over many (>10000) runs
        ByteBuffer writeBuffer = ByteBuffer.allocate(len + lengthBytes.length);
        writeBuffer.put(lengthBytes);
        writeBuffer.put(buffer);
        writeBuffer.flip();
        if (buffer != null && state == State.RUNNING) {
            int bytesWritten;
            try {
                // only 1 thread can write to a channel at a time
                SocketChannel channel = (SocketChannel)channelKey.channel();
                synchronized (channel) {
                    bytesWritten = channel.write(writeBuffer);
                }
                if (bytesWritten == -1) {
                    resetKey(channelKey);
                    disconnected(channelKey);
                }
            } catch (Exception e) {
                resetKey(channelKey);
                disconnected(channelKey);
            }
        }
    }

    private void resetKey(SelectionKey key) {
        key.cancel();
        readBuffers.remove(key);
    }
}
