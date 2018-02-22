package auth;

import server.Session;
import utils.Messages;

public class Authorization implements Messages {

    private static boolean isExist(String login){   // Проверка существования пользователя
        if (DataBase.checkLogin(login).equals("0")) Session.sendMessage(NO_SUCH_USER);
            else return true;
        return false;
    }

    public static boolean registerUser(String login, String pass){ // Регистрация пользователя
        if (!isExist(login)) {
            DataBase.registerUser(login,pass);
            Session.sendMessage(REGISTRATION_COMPLETED);
            return true;
        } else {
            Session.sendMessage(LOGIN_IS_OCCUPIED);
            return false;
        }
    }

    public static boolean validateLogin(String login, String pass){ // Checking if login is valid
        if (DataBase.getPass(login).equals(pass)){  // проверка правильности логина и пароля
            Session.sendMessage(CORRECT_PASS);
            return true;
        } else {
            Session.sendMessage(INCORRECT_PASS);
            return false;
        }
    }
}
