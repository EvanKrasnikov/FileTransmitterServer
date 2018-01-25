package basehandler;

import auth.Authorization;
import speaker.Messages;
import storage.Storage;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BaseHandler implements Messages {
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

    public synchronized void parseMassage(ConcurrentLinkedDeque<String> queue){
        switch (queue.pop()){
            case LOGIN :{
                login = queue.pop();
                pass = queue.pop();
                authorization = new Authorization();
                authorization.loginValidation();

                if (authorization.isAuthorizationOk){
                    name = login;
                    storage = new Storage(name);
                }
            }

            case REGISTER :{
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

            case ADD :{
                storage.receiveFile(queue);
            }

            case REMOVE :{
                storage.removeFile(queue);
            }

            case GET_LIST :{
                storage.sendFileList();
            }
        }
    }
}
