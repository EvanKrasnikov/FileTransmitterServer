package speaker;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class Message{
    ServerSocket socket;
    private ByteBuffer buffer;
    private ByteChannel channel;

    public void sendMessage(String msg){
        byte[] bytes = msg.getBytes();

        try {
            buffer = ByteBuffer.allocate(30);
            buffer.get(bytes);
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
