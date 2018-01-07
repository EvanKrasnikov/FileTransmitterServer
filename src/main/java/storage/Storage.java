package storage;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.EnumSet;

public class Storage {
    private static final int BUFFER_SIZE = 16384;
    private String STORAGE_PATH = "/home/user/foo";
    private String LINUX_DELIMETER = "/";
    private String name;

    public Storage(String name){
        this.name = name;
    }

    public void createFolder(String username){
        try {
            Path path = Paths.get(STORAGE_PATH + LINUX_DELIMETER + username);
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPath(){
        return (STORAGE_PATH + LINUX_DELIMETER + name + LINUX_DELIMETER);
    }

    public void receiveFile(ArrayDeque<String> arrayDeque){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(getPath() + arrayDeque.pop());

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
            }
        }
    }

    public void removeFile(ArrayDeque<String> arrayDeque){
        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(getPath() + arrayDeque.pop());

            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
