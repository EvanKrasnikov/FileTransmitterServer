package auth;

import server.Sendable;
import utils.Messages;

public class Authorization implements Messages, Sendable {
    private Sendable sender;

    public Authorization(Sendable sender){
        this.sender = sender;
    }

    private synchronized boolean isExist(String login){   // Проверка существования пользователя
        if (DataBase.checkLogin(login).equals("0")) sendMessage(NO_SUCH_USER.getBytes());
            else return true;
        return false;
    }

    public synchronized boolean registerUser(String login, String pass){ // Регистрация пользователя
        if (!isExist(login)) {
            DataBase.registerUser(login,pass);
            sendMessage(REGISTRATION_COMPLETED.getBytes());
            return true;
        } else {
            sendMessage(LOGIN_IS_OCCUPIED.getBytes());
            return false;
        }
    }

    public synchronized boolean validateLogin(String login, String pass){ // Checking if login is valid
        if (DataBase.getPass(login).equals(pass)){  // проверка правильности логина и пароля
            sendMessage(CORRECT_PASS.getBytes());
            return true;
        } else {
            sendMessage(INCORRECT_PASS.getBytes());
            return false;
        }
    }

    @Override
    public void sendMessage(byte[] bytes) {
        sender.sendMessage(bytes);
    }
}
