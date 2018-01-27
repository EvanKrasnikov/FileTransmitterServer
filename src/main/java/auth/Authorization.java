package auth;

import speaker.Message;
import speaker.Messages;


public class Authorization implements Messages {
    private String login;
    private String pass;
    private Message msg = new Message();
    public boolean isAuthorizationOk = false;
    private DataBase dataBase;

    private boolean isExist(){   // Проверка существования пользователя
        if (dataBase.checkLogin(login).equals("0")) msg.sendMessage(NO_SUCH_USER);
            else return true;
        return false;
    }

    public void registerUser(){ // Регистрация пользователя
        if (!isExist()) {
            dataBase.registerUser(login,pass);
            msg.sendMessage(REGISTRATION_COMPLETED);
            isAuthorizationOk = true;
        } else {
            msg.sendMessage(LOGIN_IS_OCCUPIED);
        }
    }

    public void loginValidation(){ // Checking if login is valid
        if (dataBase.getPass(login).equals(pass)){  // проверка правильности логина и пароля
            msg.sendMessage(CORRECT_PASS);
            isAuthorizationOk = true;
        } else {
            msg.sendMessage(INCORRECT_PASS);
        }
    }
}
