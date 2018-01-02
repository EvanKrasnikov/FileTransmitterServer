import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.util.List;

public class tmp2 {
    private Socket socket = new Socket();
    private PreparedStatement statement = null;
    private Connection connection = null;
    private String login;
    private String pass;
    private List<File> files;
    private Object object;
    private String url = "C://TMP/DB/database.db";
    private String port ="9085";
    private ObjectInputStream in;
    private ObjectOutputStream out;

    void con(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver was not found");
        }

        try {
            System.out.println("SQL driver loaded");

            connection = DriverManager.getConnection("jdbc:sqlite:C:/TMP/DB/database.db");
            System.out.println("Connection to the database established");

            statement = connection.prepareStatement("SELECT login, pass FROM users where login = ?");  // Проверка существования пользователя
            statement.setString(1,"v1gk8");
            ResultSet result = statement.executeQuery();

            System.out.println(result.getString(1));
            System.out.println(result.getString(2));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                System.out.println("Can not close the database");
            }
        }
    }
}
