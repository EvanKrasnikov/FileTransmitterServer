package server;

import auth.Authorization;
import utils.Messages;
import filemanager.FileManager;

import java.util.concurrent.ConcurrentLinkedDeque;

public class BaseHandler implements Messages {
    private String name;
    private String login;
    private String pass;
    private Session session;

    public BaseHandler(Session session) {
        this.session = session;
    }

    public synchronized void parseMassage(ConcurrentLinkedDeque<String> queue){
        switch (queue.pop()){
            case LOGIN :{
                login = queue.pop();
                pass = queue.pop();

                if (new Authorization(session).validateLogin(login,pass)) name = login;
            }

            case REGISTER :{
                login = queue.pop();
                pass = queue.pop();

                if (new Authorization(session).registerUser(login,pass)){
                    name = login;
                    FileManager.createFolder(name);
                }
            }

            case ADD : FileManager.receiveFile(name,queue);

            case REMOVE : FileManager.removeFile(name,queue);

            case GET_LIST : session.sendMessage(FileManager.getFileListAsString(name));

            case GET_FILES : FileManager.sendFiles(name,queue);
        }
    }
}
