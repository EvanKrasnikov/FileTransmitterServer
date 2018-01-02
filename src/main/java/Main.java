import auth.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static int port = 8189;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен, ожидаем подключения");

            while(true){
                socket = serverSocket.accept();
                new ClientHandler(socket);
                System.out.println("Клиент подключился");
            }

        } catch (IOException e) {
            System.err.println("Ошибка инициализации сервера");
        } finally {
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Не удалось закрыть порт");
                }
            }
        }
    }
}


