package filemanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FileManager{
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

    protected static synchronized String getPath(String username){
        return (STORAGE_PATH + LINUX_DELIMETER + username + LINUX_DELIMETER);
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
}
