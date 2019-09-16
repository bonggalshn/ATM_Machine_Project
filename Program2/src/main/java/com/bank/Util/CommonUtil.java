package com.bank.Util;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CommonUtil {
    public static String receiveFromSocket(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String receive = (String)dis.readUTF();

            socket.close();
            serverSocket.close();

            return receive;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}
