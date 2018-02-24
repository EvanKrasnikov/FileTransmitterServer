package filemanager;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FileManager{
    private static final int BUFFER_SIZE = 16384;
    private static final String STORAGE_PATH = "/home/user/foo";
    private static final String LINUX_DELIMETER = "/";

    public static synchronized void createFolder(String username){
        try {
            Path path = Paths.get(STORAGE_PATH + LINUX_DELIMETER + username);
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't create folder");
        }
    }

    private static synchronized String getPath(String username){
        return (STORAGE_PATH + LINUX_DELIMETER + username + LINUX_DELIMETER);
    }

    public static synchronized void receiveFile(String username, ConcurrentLinkedDeque<String> arrayDeque){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(getPath(username) + arrayDeque.pop());

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

    public static synchronized void removeFile(String username, ConcurrentLinkedDeque<String> arrayDeque){
        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(getPath(username) + arrayDeque.pop());

            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Can't delete file");
            }
        }
    }

    public static synchronized String getFileListAsString(String username){
        Path path = Paths.get(getPath(username));
        File folder = path.toFile();
        List<String> list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (final File file : folder.listFiles()) {
            String name = file.getName();

            Long length = file.length();
            String size = length.toString();

            Long date = file.lastModified();
            String lastModified = date.toString();

            String entry = name + " " + size + " " + lastModified + "\t";
            stringBuilder.append(entry);
        }
        return stringBuilder.toString();
    }

    public static synchronized void sendFiles(String username, ConcurrentLinkedDeque<String> arrayDeque){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (!arrayDeque.isEmpty()){
            Path path = Paths.get(getPath(username) + arrayDeque.pop());

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
                System.err.println("Can't send file");
            }
        }
    }
}
