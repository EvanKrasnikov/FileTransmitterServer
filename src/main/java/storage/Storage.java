package storage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Storage {
    private static final int BUFFER_SIZE = 16384;
    private String STORAGE_PATH = "/home/user/foo";
    private String LINUX_DELIMETER = "/";
    private String name;
    private ByteChannel channel;

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
        return (STORAGE_PATH + LINUX_DELIMETER + this.name + LINUX_DELIMETER);
    }

    public synchronized void receiveFile(ConcurrentLinkedDeque<String> arrayDeque){
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

    public synchronized void removeFile(ConcurrentLinkedDeque<String> arrayDeque){
        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(getPath() + arrayDeque.pop());

            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendFileList(){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        byte[] bytes = getFileList().toString().getBytes();
        buffer.get(bytes);

        try {
            channel.write(buffer);
        } catch (IOException e) {
            System.err.println("Can't send file list");
        }
    }

    private List getFileList(){
        Path path = Paths.get(STORAGE_PATH + LINUX_DELIMETER + this.name);
        File folder = path.toFile();
        List<String> list = new ArrayList<>();

        for (final File file : folder.listFiles()) {
            String name = file.getName();

            Long length = file.length();
            String size = length.toString();

            Long date = file.lastModified();
            String lastModified = length.toString();

            list.add(name + " " + size + " " + lastModified);
        }
        return list;
    }

    public synchronized void sendFiles(ConcurrentLinkedDeque<String> arrayDeque){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(getPath() + arrayDeque.pop());

            try {
                FileChannel fileChannel = FileChannel.open(path,StandardOpenOption.READ);

                while (fileChannel.write(buffer) > 0){
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

}
