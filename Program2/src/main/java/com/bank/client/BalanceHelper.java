package com.bank.client;

import com.bank.Util.CommonUtil;
import com.bank.Util.ISOUtil;
import com.bank.Util.MQUtil;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BalanceHelper {
    private ISOUtil isoUtil = new ISOUtil();
    private MQUtil mqUtil = new MQUtil();

    public void inquiry(String accountNumber, String pinNumber){
        String isoMessage = buildISO(accountNumber, pinNumber);

//        String result = ClientHelper.sendData(isoMessage, "http://localhost:8080/balance/info");

        // Send to message broker queue --------------------------------------------------------------------------------
        mqUtil.sendToExchange("mainExchange",isoMessage);
        String result = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));

        ISOMsg isoResult = isoUtil.stringToISO(result);

        System.out.println("\n--Balance Inquiry--------");
        if(isoResult.getString(39).equals("00"))
            System.out.println("Balance: "+isoResult.getString(62));
        else
            System.out.println("Transaksi tidak dapat dilakukan");
    }

    private String buildISO(String accountNumber, String pinNumber) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");

            isoMsg.set(2, accountNumber);
            isoMsg.set(3, "300000");
            isoMsg.set(4, "0");
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "000001");
            isoMsg.set(12, new SimpleDateFormat("HHmmss").format(new Date()));
            isoMsg.set(13, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(15, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(18, "0000");
            isoMsg.set(32, "00000000000");
            isoMsg.set(33, "00000000000");
            isoMsg.set(37, "000000000000");
            isoMsg.set(41, "12340001");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(49, "360");
            isoMsg.set(52, pinNumber);
            isoMsg.set(54, Client.getServer()+":"+Client.getPort());
            isoMsg.set(62, "0");
            isoMsg.set(102, "0");

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
