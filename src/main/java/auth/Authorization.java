package auth;

import auth.ClientHandler;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

class Authorization extends ClientHandler{
    private Connection connection;
    private PreparedStatement statement;
    private String url = "C:/TMP/DB/database.db";
    private String port = "5050";
    private String login;
    private String pass;
    private String storagePath = "C:/tmp/";
    private ObjectOutputStream out;
    private ObjectInputStream in;
    //private Socket socket;
    //private ServerSocket server;


    public void setLogin(String login) {
        this.login = login;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    Authorization(Socket socket, ServerSocket server){
        super(socket, server);
        //this.socket = socket;
        //this.server = server;
    }

    private void connectToDB(){
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQL driver loaded");

            connection = DriverManager.getConnection("jdbc:sqlite:" + url + ":" + port);
            System.out.println("Connection to the database established");

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnectWithDB(){
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Can not close the database");
        }
    }

    private boolean isExist(){
        try {
            statement = connection.prepareStatement("SELECT count(*) FROM users where login = ?");  // Проверка существования пользователя
            statement.setString(1,login);
            ResultSet result = statement.executeQuery();

            if (result.getString(1).equals("0")) {
                out.writeUTF("/nosuchuser");
            } else return true;

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    void registerUser(){
        connectToDB();

        if (!isExist()) {
            try {
                statement = connection.prepareStatement("INSERT INTO users (login, pass) VALUES (?,?)");  // Регистрация пользователя
                statement.setString(1,login);
                statement.setString(2,pass);
                createFolder();
                out.writeUTF("/registrationok");
                setisAUthorizationOK = true;
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }

        } else {

            try {
                out.writeUTF("/loginisoccupied");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        disconnectWithDB();
    }

    void loginValidation(){
        connectToDB();

        try {
            statement = connection.prepareStatement("SELECT login, pass FROM users where login = ?");
            statement.setString(1,login);
            ResultSet result = statement.executeQuery();

            if ((!result.getString(1).equals(login)) || (!result.getString(2).equals(pass))){  // проверка правильности логина и пароля
                out.writeUTF("/incorrectpass");
            } else {
                out.writeUTF("/correctpass");
                isAUthorizationOK = true;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        disconnectWithDB();
    }

    private void createFolder(){
        new File(storagePath + login + "/").mkdirs();
    }
}
