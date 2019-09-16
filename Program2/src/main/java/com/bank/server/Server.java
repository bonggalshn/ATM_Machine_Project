package com.bank.server;

import com.bank.Util.ISOUtil;
import com.bank.Util.MQUtil;
import com.bank.controller.BalanceController;
import com.bank.service.BalanceService;
import com.rabbitmq.client.*;
import org.jpos.iso.ISOMsg;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Server {
    private ISOUtil isoUtil = new ISOUtil();
    @Autowired
    private BalanceController balanceController;

    @RabbitListener(queues = "mainQueue")
    public void getResponse(byte[] message){
        String response = new String(message);
        ISOMsg isoMessage = isoUtil.stringToISO(response);
        String processingCode = isoMessage.getString(3);
        controllerMapping(processingCode,response);
    }

    private void controllerMapping(String processingCode, String message){
        String response = "";
        switch (processingCode){
            case "300000":
                response = balanceController.BalanceInfo(message);
                sendISOViaSocket(response);
                break;
        }
//        System.out.println("response: "+response);
    }

    private void sendISOViaSocket(String message){
        try {

            ISOMsg isoMessage = isoUtil.stringToISO(message);
            String address[] = isoMessage.getString(54).split(":");

            Socket socket = new Socket(address[0], Integer.parseInt(address[1]));
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            dout.writeUTF(message);

            dout.flush();
            dout.close();
            socket.close();
        }catch (Exception e){
            e.getMessage();
        }
    }

}
