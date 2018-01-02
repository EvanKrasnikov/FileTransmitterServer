import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class TestingDB extends Assert {
    private PreparedStatement statement = null;
    private Connection connection = null;
    private String login;
    private String pass;
    private String url = "C:/TMP/DB/database.db";
    private String port ="9085";


    @Before
    public void run() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver was not found");
        }
        try {
            System.out.println("SQL driver loaded");

            connection = DriverManager.getConnection("jdbc:sqlite:" + url + ":" + port);
            System.out.println("Connection to the database established");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void registerTest(){
        try {
            statement = connection.prepareStatement("INSERT INTO users (login, pass) VALUES (?,?)");  // Регистрация пользователя
            statement.setString(1,login);
            statement.setString(2,pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void existTest(){
        try {
            statement = connection.prepareStatement("SELECT count(*) FROM users where login = ?");  // Проверка существования пользователя
            statement.setString(1,login);
            ResultSet result = statement.executeQuery();
            assertEquals("0",result.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    private void loginValidation(){
        try {
            statement = connection.prepareStatement("SELECT login, pass FROM users where login = ?");
            statement.setString(1,login);
            ResultSet result = statement.executeQuery();
            assertFalse((!result.getString(1).equals(login)) || (!result.getString(2).equals(pass)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void close(){
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Can not close the database");
        }
    }
}

