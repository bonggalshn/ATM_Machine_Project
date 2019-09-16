package com.bank.server;

import com.bank.Util.ISOUtil;
import com.bank.controller.*;
import org.jpos.iso.ISOMsg;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.DataOutputStream;
import java.net.Socket;

public class Server {
    private ISOUtil isoUtil = new ISOUtil();

    @Autowired
    private BalanceController balanceController;
    @Autowired
    private WithdrawalController withdrawalController;
    @Autowired
    private TransferController transferController;


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
                break;
            case "010000":
                response = withdrawalController.withdraw(message);
                break;
            case "390000":
                response = transferController.transferInternalInquiry(message);
                break;
            case "400000":
                response = transferController.transferInternal(message);
                break;
            case "391000":
                response = transferController.transferExternalInquiry(message);
                break;
            case "401000":
//                System.out.println("Transfer external");
                response = transferController.transferExternal(message);
                System.out.println("akan dikirim ke atm: "+response);
                break;
        }
        sendISOViaSocket(response);
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
