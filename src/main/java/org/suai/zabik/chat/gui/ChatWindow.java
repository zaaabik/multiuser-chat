package org.suai.zabik.chat.gui;

import org.suai.zabik.chat.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatWindow extends JFrame {
    private static final int XSIZE = 400;
    private static final int YSIZE = 400;
    private JTextField inputText;
    private transient Client client;
    private JTextArea allMsg;

    public ChatWindow(String windowName, Client client){
        super(windowName);
        this.client = client;
        initUi();
    }

    private void initUi(){
        setSize(XSIZE, YSIZE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel sendMsgPanel = new JPanel();
        inputText = new JTextField(25);
        sendMsgPanel.add(inputText);

        allMsg = new JTextArea();

        getContentPane().add(BorderLayout.CENTER,allMsg);
        getContentPane().add(BorderLayout.SOUTH,sendMsgPanel);
        sendMsgByEnterPress();
        setVisible(true);
        new Thread(this::showReceivedMsg).start();
    }

    private void sendMsgByEnterPress(){
        inputText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && inputText.getText().length() != 0) {
                    client.sendMsg(inputText.getText());
                    inputText.setText("");
                }
            }
        });
    }

    private void showReceivedMsg(){
        String msg;
        while (true){
            msg = client.getMsg();
            if(msg.equals("exit")){
                break;
            }
            if(msg.length() != 0){
                allMsg.append(msg + System.lineSeparator());
            }
        }
    }

}