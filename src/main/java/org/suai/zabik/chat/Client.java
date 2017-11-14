package org.suai.zabik.chat;

import org.suai.zabik.chat.gui.ChatWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private Socket socket;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());


    private Client(String rawAddress, int userPort) {
        try {
            socket = new Socket(InetAddress.getByName(rawAddress), userPort);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }

    }

    public void sendMsg(String msg) {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
            printWriter.println(msg);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
    }

    public String getMsg() {
        String msg = "";
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msg = inputReader.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
        return msg;
    }

    public static void main(String []args) {
        Client client = new Client("localhost", 1337);
        new ChatWindow("user", client);
    }
}