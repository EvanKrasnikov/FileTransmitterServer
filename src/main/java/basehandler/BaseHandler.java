package basehandler;

import auth.Authorization;
import storage.Storage;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayDeque;

public class BaseHandler {
    private ServerSocketChannel socket;
    private SelectionKey key;
    private String name;
    private String login;
    private String pass;
    private Authorization authorization;
    private Storage storage;

    public BaseHandler(ServerSocketChannel socket, SelectionKey key) {
        this.socket = socket;
        this.key = key;
        name = "";
    }

    public void parseMassage(ArrayDeque<String> queue){
        switch (queue.pop()){
            case "/login" :{
                login = queue.pop();
                pass = queue.pop();
                authorization = new Authorization();
                authorization.loginValidation();

                if (authorization.isAuthorizationOk){
                    name = login;
                    storage = new Storage(name);
                }
            }

            case "/register" : {
                login = queue.pop();
                pass = queue.pop();
                authorization = new Authorization();
                authorization.registerUser();

                if (authorization.isAuthorizationOk){
                    name = login;
                    storage = new Storage(name);
                    storage.createFolder(name);
                }
            }

            case "/add" :{
                storage.receiveFile(queue);
            }

            case "/remove" : {
                storage.removeFile(queue);
            }
        }
    }
}
