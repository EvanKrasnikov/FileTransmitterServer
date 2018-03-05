package filemanager;

import server.Sendable;
import server.Session;
import utils.Messages;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FileSync implements Sendable{
    private static final int BUFFER_SIZE = 16384;
    private Session session;

    public FileSync(Session session) {
        this.session = session;
    }

    public static synchronized void receiveFile(String username, ConcurrentLinkedDeque<String> arrayDeque){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(FileManager.getPath(username) + arrayDeque.pop());

            try {
                FileChannel fileChannel = FileChannel.open(path,
                        EnumSet.of(StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING,
                                StandardOpenOption.WRITE)
                );

                while (fileChannel.read(buffer) > 0){
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                }

                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Can't receive file");
            }
        }
    }

    public synchronized void sendFiles(String username, ConcurrentLinkedDeque<String> arrayDeque){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(FileManager.getPath(username) + arrayDeque.pop());

            buffer.get((Messages.FILE + " " + path.getName(0)).getBytes());
            try {
                session.getChannel().write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Can't send header");
            } finally {
                buffer.clear();
            }

            try {
                FileChannel fileChannel = FileChannel.open(path,StandardOpenOption.READ);

                while (fileChannel.write(buffer) > 0){
                    buffer.flip();
                    session.getChannel().write(buffer);
                    buffer.clear();
                }

                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Can't send file");
            }
        }
    }

    @Override
    public void sendMessage(byte[] bytes) {
        session.sendMessage(bytes);
    }
}
