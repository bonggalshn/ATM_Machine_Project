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

public class TransferHelper {
    private static final Logger logger = LoggerFactory.getLogger(TransferHelper.class);
    private String accountNumber, pinNumber;
    private ISOUtil isoUtil = new ISOUtil();

    void TransferMain(String accountNumber, String pinNumber) {
        this.accountNumber = accountNumber;
        this.pinNumber = pinNumber;

        int entry = 0;
        try {
            transferMenu();

            System.out.print("Entry: ");
            entry = Integer.parseInt(ClientHelper.read());
            transferCase(entry);
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan. \nTransaksi tidak dapat dilakukan");
            logger.error(e.getMessage());
        }
    }

    private void transferMenu() {
        System.out.println("\n--Transfer----------");
        System.out.println("1. Ke Sesama Bank");
        System.out.println("2. Ke Bank Lain");
        System.out.println("3. Batal");
        System.out.println("----------------------");
    }

    private void transferCase(int entry) {
        String message;
        switch (entry) {
            case 1:
                message = transferInquiry();
                if (message.equals(null)) {
                    System.out.println("Transaksi tidak dapat dilakukan");
                    break;
                }
                if (isoUtil.checkISO(message)) {
                    transferProcess(message);
                } else {
                    System.out.println("Transaksi tidak dapat dilakukan.");
                }
                break;
            case 2:
                message = transferExternalInquiry();
                if (message.equals(null) || message.equals("05")) {
                    System.out.println("Transaksi tidak dapat dilakukan.");
                    break;
                }
                transferExternalProcess(message);
                break;
            case 3:
                System.out.println("Transaksi dibatalkan");
                break;
            default:
                System.out.println("Masukan salah.");
                break;
        }
    }

    private String transferInquiry() {
        MQUtil mqUtil = new MQUtil();
        System.out.print("Masukkan nomor rekening pemindahbukuan: ");
        String tujuan = ClientHelper.read();
        System.out.print("Masukkan jumlah yang akan ditransfer: ");
        int amount = Integer.parseInt(ClientHelper.read());
        String message = buildISOInquiry(tujuan, amount);

//        String response = ClientHelper.sendData(message, "http://localhost:8080/transfer/internalInquiry");
        mqUtil.sendToExchange("mainExchange", message);
        String response = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));

        return response;
    }

    private void transferProcess(String message) {
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        if (!isoMessage.getString(39).equals("00")) {
            System.out.println("Transaksi tidak dapat dilakukan");
            return;
        }

        String nama = isoMessage.getString(103);
        String rekTujuan = isoMessage.getString(102);
        String jumlah = isoMessage.getString(4);

        System.out.println("\n\n------------------------------------");
        System.out.println("Anda akan melakukan transfer kepada:");
        System.out.println("Nama           : " + nama);
        System.out.println("Nomor Rekening : " + rekTujuan);
        System.out.println("Jumlah         : " + (Integer.parseInt(jumlah)));

        System.out.println("Apakah anda yakin?");
        System.out.println("1. Ya");
        System.out.println("2. Tidak");

        int entry = 0;
        here:
        do {
            try {
                System.out.print("Entry: ");
                entry = Integer.parseInt(ClientHelper.read());
                switch (entry) {
                    case 1:
                        String transferMessage = buildISOTransfer(rekTujuan, Integer.parseInt(jumlah));
//                        String transferMessage = buildISOTransfer(isoMessage);
//                        String response = ClientHelper.sendData(transferMessage, "http://localhost:8080/transfer/internal");

                        MQUtil mqUtil = new MQUtil();
                        mqUtil.sendToExchange("mainExchange", transferMessage);
                        String response = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));

                        ISOMsg result = isoUtil.stringToISO(response);

                        if (result.getString(39).equals("00")) {
                            logger.info("Account '{}' perform '{}' CASH WITHDARWAL", result.getString(2), Integer.parseInt(result.getString(4)));
                            System.out.println("Transaksi Berhasil");
                        } else {
                            System.out.println("Transaksi Gagal");
                        }
                        break here;
                    case 2:
                        System.out.println("Transaksi dibatalkan.");
                        break here;
                    default:
                        System.out.println("Masukan salah");
                        break here;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                break here;
            }
        } while (true);
    }

    private String buildISOInquiry(String tujuan, int jumlah) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");

            isoMsg.set(2, this.accountNumber);
            isoMsg.set(3, "390000");
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
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "360");
            isoMsg.set(52, this.pinNumber);
            isoMsg.set(54, Client.getServer() + ":" + Client.getPort());
            isoMsg.set(62, "0");
            isoMsg.set(100, "001");
            isoMsg.set(102, tujuan);//rek tujuan
            isoMsg.set(103, "0");//nama
            isoMsg.set(125, "0");
            isoMsg.set(127, "001");

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    private String buildISOTransfer(String tujuan, int jumlah) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");
//            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(2, this.accountNumber);
            isoMsg.set(3, "400000");
            isoMsg.set(4, "" + jumlah);
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
            isoMsg.set(48, "0");
            isoMsg.set(49, "360");
            isoMsg.set(52, this.pinNumber);
            isoMsg.set(54, Client.getServer() + ":" + Client.getPort());
            isoMsg.set(62, "0");
            isoMsg.set(100, "001");
            isoMsg.set(102, tujuan);//rek tujuan
            isoMsg.set(103, "0");//nama
            isoMsg.set(125, "0");
            isoMsg.set(127, "001");

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    private String transferExternalInquiry() {
        System.out.print("Masukkan 3 digit kode bank dan nomor rekening tujuan: ");
        String tujuan = ClientHelper.read();
        System.out.print("Masukkan jumlah yang akan ditransfer : ");
        int jumlah = Integer.parseInt(ClientHelper.read());

        String bankCode = tujuan.substring(0, 3);
        tujuan = tujuan.substring(3);

        String message = buildISOInquiryExternal(tujuan, jumlah, bankCode);

//        String response = ClientHelper.sendData(message, "http://localhost:8080/transfer/externalInquiry");

        String response = "";
        MQUtil mqUtil = new MQUtil();
        mqUtil.sendToExchange("mainExchange", message);
        response = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));

        return response;
    }

    private String buildISOInquiryExternal(String tujuan, int jumlah, String bankCode) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");

            isoMsg.set(2, this.accountNumber);
            isoMsg.set(3, "391000");
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
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "360");
            isoMsg.set(52, this.pinNumber);
            isoMsg.set(54, Client.getServer() + ":" + Client.getPort());
            isoMsg.set(62, "0");
            isoMsg.set(100, "001");
            isoMsg.set(102, tujuan);//rek tujuan
            isoMsg.set(103, "0");//nama
            isoMsg.set(125, "0");
            isoMsg.set(127, bankCode);

            byte[] result = isoMsg.pack();

            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return e.getMessage();
        }
    }

    private void transferExternalProcess(String message) {
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        if (!isoMessage.getString(39).equals("00")) {
            System.out.println("Transaksi tidak dapat dilakukan");
            return;
        }

//        isoUtil.printISOMessage(isoMessage);

        System.out.println("\n\n--------------------------------");
        System.out.println("Anda akan melakukan transfer berikut.");
        System.out.println("Tujuan       : " + isoMessage.getString(103));
        System.out.println("No. Rekening : " + isoMessage.getString(102));
        System.out.println("Jumlah       : " + Integer.parseInt(isoMessage.getString(4)));
        System.out.println("*Jumlah telah termasuk charge transfer ke bank lain.");

        System.out.println("Apakah Anda ingin melanjutkan transaksi ?");
        System.out.println("1. Ya");
        System.out.println("2. Tidak");

        here:
        do {
            try {
                System.out.print("Entry: ");
                int entry = Integer.parseInt(ClientHelper.read());
                switch (entry) {
                    case 1:
                        String transferMessage = buildISOExternalTransfer(message);
//                        String response = ClientHelper.sendData(transferMessage, "http://localhost:8080/transfer/external");
                        MQUtil mqUtil = new MQUtil();
                        mqUtil.sendToExchange("mainExchange", transferMessage);
                        String response = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));

                        ISOMsg isoResponse = isoUtil.stringToISO(response);

                        if (isoResponse.getString(39).equals("00"))
                            System.out.println("\n\nTransaksi berhasil");
                        else
                            System.out.println("\n\nTransaksi gagal");
                        break here;
                    case 2:
                        System.out.println("Transaksi dibatalkan.");
                        break here;
                    default:
                        System.out.println("Masukan salah");
                        break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } while (true);
    }

    private String buildISOExternalTransfer(String message) {
        ISOMsg isoMessage = isoUtil.stringToISO(message);
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "401000");
            isoMsg.set(4, isoMessage.getString(4));
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
            isoMsg.set(48, "0");
            isoMsg.set(49, "360");
            isoMsg.set(52, this.pinNumber);
            isoMsg.set(54, Client.getServer() + ":" + Client.getPort());
            isoMsg.set(62, "0");
            isoMsg.set(100, "001");
            isoMsg.set(102, isoMessage.getString(102));//rek tujuan
            isoMsg.set(103, "0");//nama
            isoMsg.set(125, "0");
            isoMsg.set(127, isoMessage.getString(127));

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
