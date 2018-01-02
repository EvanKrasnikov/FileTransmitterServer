package storage;

import auth.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.List;

class SyncFiles implements Runnable {
    private Socket socket;
    private List<File> files;
    //ObjectInputStream in;
    //ObjectOutputStream out;

    SyncFiles(Socket socket, List<File> files) {
        this.socket = socket;
        this.files = files;
    }

    public void run(){

        try {
            while (true){
                Object request;
                while (true){
                    request = in.readObject();

                    if (request instanceof File){
                        File requestFile = (File)request;
                        System.out.println("File " + requestFile.getName() + " received!");

                        if (files.contains(requestFile)){
                            files.remove(requestFile);
                            System.out.println("There are " + files.size() + " after deletion");
                        } else {
                            files.add(requestFile);
                            System.out.println("There are " + files.size() + " after addition");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Can't synchronize with the client");
        }

    }

    void getFiles(){
        System.out.println("The command 'get' received from");

        try {
            out.writeObject(files);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Files were synchronized");
    }



}
