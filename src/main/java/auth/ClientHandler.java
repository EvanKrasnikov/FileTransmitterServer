package auth;

import storage.SyncFiles;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.List;

public class ClientHandler implements Runnable {
    Socket socket;
    String login;
    String pass;

    protected List<File> files;
    Object object;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Boolean isAuthorizationOK = false;
    Authorization authorization;


    public void setAuthorizationOK(Boolean AUthorizationOK) {
        isAuthorizationOK = AUthorizationOK;
    }

    public ClientHandler(Socket socket, Server server){
        try {
            this.socket = socket;
            this.server = server;
            name = "";
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!isAuthorizationOK) {
                object = in.readObject();
                parseMassage();
            }

            //SyncFiles sync = new SyncFiles(this.socket,files);  // нужно передать сокет и список файлов
            //sync.start();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parseMassage(){
        if (object instanceof String){ // разбивка на слова
            String[] strings = ((String) object).split("\\s");
            login = strings[1];
            pass = strings[2];

            switch (strings[0]){
                case "/login" : authorization.loginValidation();
                case "/register" : authorization.registerUser();
                case "/get" : {
                    //SyncFiles sync = new SyncFiles()
                }
                //case "/disconnect" : authorization.registerUser();
            }
        }
    }
}
