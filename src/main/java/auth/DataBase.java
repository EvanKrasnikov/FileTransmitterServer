package auth;

import java.sql.*;

class DataBase{
    private Connection connection;
    private PreparedStatement statement;
    private final String DATABASE_PATH = "C:/TMP/DB/database.db";
    private final String PORT = "5050";

    private void connectToDB(){ // Подключение к базе данных
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQL driver loaded");

            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH + ":" + PORT);
            System.out.println("Connection to the database established");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Failed to connect to the database");
        }
    }

    private void disconnectWithDB(){ // Отключение от базы данных
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Failed to disconnect from the database");
        }
    }

    String checkLogin(String login){   // Проверка существования пользователя
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
        return "/failtoconnect";
    }

    void registerUser(String login, String pass){ // Регистрация пользователя
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

    String getPass(String login){ // Checking if login is valid
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
        return "/failtoconnect";
    }
}
