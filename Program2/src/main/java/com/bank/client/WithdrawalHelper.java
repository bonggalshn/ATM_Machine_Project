package com.bank.client;

import com.bank.Util.CommonUtil;
import com.bank.Util.ISOUtil;
import com.bank.Util.MQUtil;
import org.jpos.iso.ISOMsg;

import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WithdrawalHelper {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawalHelper.class);
    private String accountNumber, pinNumber;
    private ISOUtil isoUtil= new ISOUtil();

    void withdrawalMain(String accountNumber, String pinNumber) {
        try {
            this.accountNumber = accountNumber;
            this.pinNumber = pinNumber;
            withdrawalMenu();
            System.out.print("Entry: ");
            int entry = Integer.parseInt(ClientHelper.read());
            withdrawalCase(entry);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void withdrawalMenu() {
        System.out.println("\n\n--Tarik Tunai----------");
        System.out.println("1. 50.000              2. 100.000");
        System.out.println("3. 200.000             4. 400.000");
        System.out.println("5. Jumlah lainnya      6. Batal");
    }

    private void withdrawalCase(int entry) {
        switch (entry) {
            case 1:
                withdrawal(50000);
                break;
            case 2:
                withdrawal(100000);
                break;
            case 3:
                withdrawal(200000);
                break;
            case 4:
                withdrawal(400000);
                break;
            case 5:
                System.out.print("Masukkan jumlah yang akan ditarik: ");
                int amount = Integer.parseInt(ClientHelper.read());
                withdrawal(amount);
                break;
            case 6:
                System.out.println("Kembali ke menu sebelumnya.");
                break;
            default:
                System.out.println("Masukan salah.");
                break;
        }
    }

    public void withdrawal(int amount) {
        try {
            MQUtil mqUtil = new MQUtil();
            String message = buildISO(amount);

//            String response = ClientHelper.sendData(message.toString(),"http://localhost:8080/withdrawal/withdraw");
            mqUtil.sendToExchange("mainExchange",message);
            String response = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));

            ISOMsg isoMessange = isoUtil.stringToISO(response);
            if(isoMessange.getString(39).equals("00")){
                logger.info("Cash withdrawal ({}), Account '{}', amount '{}'",
                        isoMessange.getString(54),
                        isoMessange.getString(2),
                        Integer.parseInt(isoMessange.getString(4)));
                System.out.println("Tarik tunai berhasil.");
            }else {
                if(isoMessange.getString(39).equals("51"))
                    System.out.println("Saldo Anda tidak mencukupi.");
                else
                    System.out.println("Transaksi tidak dapat dilakukan.");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String buildISO(int jumlah) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");

            isoMsg.set(2, this.accountNumber);
            isoMsg.set(3, "010000");
            isoMsg.set(4, jumlah + "");
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
            isoMsg.set(42, "000000000000000");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(49, "360");
            isoMsg.set(52, this.pinNumber);
            isoMsg.set(54, Client.getServer()+":"+Client.getPort());
            isoMsg.set(62, "0");
            isoMsg.set(102, "0");

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
