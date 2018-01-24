package auth;

import speaker.Message;

public class Authorization{
    private String login;
    private String pass;
    private Message msg = new Message();
    public boolean isAuthorizationOk = false;
    private DataBase dataBase;

    private boolean isExist(){   // Проверка существования пользователя
        if (dataBase.checkLogin(login).equals("0")) msg.sendMessage("/nosuchuser");
            else return true;
        return false;
    }

    public void registerUser(){ // Регистрация пользователя
        if (!isExist()) {
            dataBase.registerUser(login,pass);
            msg.sendMessage("/registrationok");
            isAuthorizationOk = true;
        } else {
            msg.sendMessage("/loginisoccupied");
        }
    }

    public void loginValidation(){ // Checking if login is valid
        if (dataBase.getPass(login).equals(pass)){  // проверка правильности логина и пароля
            msg.sendMessage("/correctpass");
            isAuthorizationOk = true;
        } else {
            msg.sendMessage("/incorrectpass");
        }
    }
}
