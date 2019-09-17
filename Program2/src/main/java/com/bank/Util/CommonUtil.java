package com.bank.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
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
            logger.error(e.getMessage());
            return e.getMessage();
        }
    }
}
