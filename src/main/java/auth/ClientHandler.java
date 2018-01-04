package auth;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class ClientHandler{
    private ServerSocketChannel socket;
    private SelectionKey key;
    private String name;
    String login;
    String pass;

    private Boolean isAuthorizationOK = false;
    Authorization authorization;

    public ClientHandler(ServerSocketChannel socket, SelectionKey key){
        this.socket = socket;
        this.key = key;
        name = "";
    }

    public void setAuthorizationOK(Boolean AUthorizationOK) {
        isAuthorizationOK = AUthorizationOK;
    }

    public void parseMassage(String message){
        String[] strings = message.split("\\s");

        if (strings.length >= 3){
            login = strings[1];
            pass = strings[2];
        }

        switch (strings[0]){
            case "/login" :{
                authorization = new Authorization();
                authorization.loginValidation();
            }

            case "/register" : {
                authorization = new Authorization();
                authorization.registerUser();
            }

            case "/get" : {
                //SyncFiles sync = new SyncFiles()
            }
            //case "/disconnect" : authorization.registerUser();
        }
    }
}
