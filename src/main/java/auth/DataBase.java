package auth;

import utils.Messages;

import java.sql.*;

class DataBase implements Messages{
    private static Connection connection;
    private static PreparedStatement statement;
    private static final String DATABASE_PATH = "C:/TMP/DB/database.db";
    private static final String PORT = "5050";

    private synchronized static void connectToDB(){ // Подключение к базе данных
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQL driver loaded");

            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH + ":" + PORT);
            System.out.println("Connection to the database established");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Failed to connect to the database");
        }
    }

    private synchronized static void disconnectWithDB(){ // Отключение от базы данных
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Failed to disconnect from the database");
        }
    }

    static synchronized String checkLogin(String login){   // Проверка существования пользователя
        try {
            connectToDB();
            statement = connection.prepareStatement("SELECT count(*) FROM users where login = ?");
            statement.setString(1,login);
            ResultSet result = statement.executeQuery();
            return result.getString(1);
        } catch (SQLException e) {
            System.err.println("Failed to check the login existance");
        } finally {
            disconnectWithDB();
        }
        return FAILED_TO_CONNECT;
    }

    static synchronized void registerUser(String login, String pass){ // Регистрация пользователя
        try {
            connectToDB();
            statement = connection.prepareStatement("INSERT INTO users (login, pass) VALUES (?,?)");
            statement.setString(1,login);
            statement.setString(2,pass);
        } catch ( SQLException  e){
            System.err.println("Failed to register user");
        } finally {
            disconnectWithDB();
        }
    }

    static synchronized String getPass(String login){ // Checking if login is valid
        try {
            connectToDB();
            statement = connection.prepareStatement("SELECT pass FROM users where login = ?");
            statement.setString(1,login);
            ResultSet result = statement.executeQuery();
            return result.getString(1);
        } catch ( SQLException e) {
            System.err.println("Failed to validate login");
        } finally {
            disconnectWithDB();
        }
        return FAILED_TO_CONNECT;
    }
}
