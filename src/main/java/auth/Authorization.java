package auth;

import speaker.Message;

import java.sql.*;

public class Authorization{
    private Connection connection;
    private PreparedStatement statement;
    private final String DATABASE_PATH = "C:/TMP/DB/database.db";
    private final String PORT = "5050";
    private String login;
    private String pass;
    private Message msg = new Message();
    public boolean isAuthorizationOk = false;

    private void connectToDB() throws ClassNotFoundException, SQLException{ // Подключение к базе данных
        Class.forName("org.sqlite.JDBC");
        System.out.println("SQL driver loaded");

        connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH + ":" + PORT);
        System.out.println("Connection to the database established");
    }

    private void disconnectWithDB() throws SQLException{ // Отключение от базы данных
        statement.close();
        connection.close();
    }

    private boolean isExist() throws SQLException{   // Проверка существования пользователя
        statement = connection.prepareStatement("SELECT count(*) FROM users where login = ?");
        statement.setString(1,login);
        ResultSet result = statement.executeQuery();

        if (result.getString(1).equals("0")) {
            msg.sendMessage("/nosuchuser");
        } else return true;

        return false;
    }

    public void registerUser(){ // Регистрация пользователя
        try {
            connectToDB();

            if (!isExist()) {
                statement = connection.prepareStatement("INSERT INTO users (login, pass) VALUES (?,?)");
                statement.setString(1,login);
                statement.setString(2,pass);
                msg.sendMessage("/registrationok");
                isAuthorizationOk = true;
            } else {
                msg.sendMessage("/loginisoccupied");
            }


        } catch (ClassNotFoundException | SQLException  e){
            e.printStackTrace();
        } finally {
            try {
                disconnectWithDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loginValidation(){ // Checking if login is valid
        try {
            connectToDB();

            statement = connection.prepareStatement("SELECT login, pass FROM users where login = ?");
            statement.setString(1,login);
            ResultSet result = statement.executeQuery();

            if ((!result.getString(1).equals(login)) || (!result.getString(2).equals(pass))){  // проверка правильности логина и пароля
                msg.sendMessage("/incorrectpass");
            } else {
                msg.sendMessage("/incorrectpass");
                isAuthorizationOk = true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                disconnectWithDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
