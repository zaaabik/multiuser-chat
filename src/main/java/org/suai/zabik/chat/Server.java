package org.suai.zabik.chat;

import org.suai.zabik.chat.database.Database;
import org.suai.zabik.chat.database.Mongo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private int port;
    private int userId;
    private Map<String, Socket> users;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private Database db;

    Server(int serverPort, Database database) {
        db = database;
        port = serverPort;
        userId = 0;
        users = new HashMap<>();
    }

    private void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket userSocket = serverSocket.accept();
                if(userId == Integer.MAX_VALUE){
                    break;
                }
                if (userSocket != null) {
                    addChatUser(userSocket);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
    }

    private void addChatUser(Socket userSocket) {
        String userName = "user" + userId;
        users.put(userName, userSocket);
        userId++;
        LOGGER.log(Level.INFO, "user with id= %s is connected",userId);
        UserThread userThread = new UserThread(this, userSocket, userName);
        new Thread(userThread::listener).start();
    }

    String getAllGrades(String studentName){
        return db.getAllGrades(studentName);
    }

    void setExamGrade(String userName,String examName, int grade){
        db.setExamGrade(userName,examName,grade);
    }

    void addStudent(String name){
        db.addStudent(name);
    }

    String getAllStudents(){
        return db.getAllStudents();
    }

    synchronized boolean sendPrivateMsg(String userFrom, String userName, String msg) {
        Socket userSocket = users.get(userName);
        String fullMsg = userFrom + ":" + msg;
        if (userSocket == null) {
            return false;
        }
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(userSocket.getOutputStream(),true);
            printWriter.println(fullMsg );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }

    synchronized void sendMsgAll(String msg, String userName) {
        try {
            for (Map.Entry<String, Socket> user :
                    users.entrySet()) {
                OutputStream userOutStream = user.getValue().getOutputStream();
                new PrintWriter(userOutStream, true).println(userName + ":" + msg);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
    }

    public static void main(String[] args) {
        final int port = 1337;
        Database db = new Mongo();
        Server server = new Server(port,db);
        new Thread(server::startServer).start();
    }

}
