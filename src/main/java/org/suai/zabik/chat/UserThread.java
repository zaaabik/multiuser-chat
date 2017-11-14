package org.suai.zabik.chat;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class UserThread {
    private Server server;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private BufferedReader bufferedReader;
    private  String name;
    private static final String PRIVATE_SEND_COMMAND = "@senduser";
    private static final String GET_GRADES_COMMAND = "@get";
    private static final String SET_GRADES_COMMAND = "@set";
    private static final String GET_STUDENTS_COMMAND = "@all";
    private static final String ADD_STUDENTS_COMMAND = "@add";

    UserThread(Server server, Socket socket, String userName) {
        this.server = server;
        name = userName;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
    }


    void listener() {
        LOGGER.log(Level.SEVERE, "listener has started!");
        Scanner scanner = null;
        while (true) {
            try {
                String msg = bufferedReader.readLine();
                if (msg == null || msg.length() == 0) {
                    return;
                }
                scanner = new Scanner(msg);
                String command = scanner.next();
                if (msg.length() > PRIVATE_SEND_COMMAND.length() && command.equals(PRIVATE_SEND_COMMAND)) {
                    String userName = scanner.next();
                    boolean userIsExist = server.sendPrivateMsg(name, userName, scanner.nextLine());
                    if (!userIsExist) {
                        server.sendPrivateMsg("SYSTEM", name, userName + " doesn`t exist");
                    }

                }else if (msg.length() > GET_GRADES_COMMAND.length() && command.equals(GET_GRADES_COMMAND)) {
                    String userName = scanner.next();
                    server.sendPrivateMsg("server:", name, String.join(",", server.getAllGrades(userName)));

                }else if (msg.length() > SET_GRADES_COMMAND.length() && command.equals(SET_GRADES_COMMAND)) {
                    String userName = scanner.next();
                    String examName = scanner.next();
                    String rawGrade = scanner.next();
                    try {
                        int grade = Integer.parseInt(rawGrade);
                        server.setExamGrade(userName,examName,grade);
                    }catch (NumberFormatException e){
                        server.sendPrivateMsg("server", name,"wrong exam grade!");
                    }
                }else if (msg.length() >= GET_STUDENTS_COMMAND.length() && command.equals(GET_STUDENTS_COMMAND)) {
                    server.sendPrivateMsg("server",name,server.getAllStudents());
                }else if(msg.length() >= ADD_STUDENTS_COMMAND.length() && command.equals(ADD_STUDENTS_COMMAND)) {
                    String userName = scanner.next();
                    server.addStudent(userName);
                }else{
                    server.sendMsgAll(msg, name);
                }
                scanner.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }
    }
}