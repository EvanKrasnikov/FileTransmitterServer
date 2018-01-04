package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Storage {

    public void createFolder(String username) throws IOException{
        String DATABASE_PATH = "/home/user/foo";
        String LINUX_DELIMETER = "/";
        Path path = Paths.get(DATABASE_PATH + LINUX_DELIMETER + username);
        Files.createDirectories(path);
    }
}
