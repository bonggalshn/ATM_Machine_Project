package com.bank.client;

import ch.qos.logback.core.net.server.Client;
import com.bank.Util.ISOUtil;
import com.bank.client.purchase.PhoneCreditPurchase;
import org.jpos.iso.ISOMsg;

public class PurchaseHelper {
    private String accountNumber;
    private String pinNumber;
    private ISOUtil isoUtil = new ISOUtil();

    public PurchaseHelper(String accountNumber, String pinNumber) {
        this.accountNumber = accountNumber;
        this.pinNumber = pinNumber;
    }

    public void purchaseMain() {
        int entry = 0;
        here:
        do {
            System.out.println("\n\n------------------------");
            System.out.println("Pembelian");
            System.out.println("1. Pulsa");
            System.out.println("2. Batal");
            System.out.println("---------------------------");
            try {
                System.out.print("Entry: ");
                entry = Integer.parseInt(ClientHelper.read());
                purchaseCase(entry);
                if (entry == 2)
                    break here;
            } catch (Exception e) {
                System.out.println("purchange menu entry error\n" + e.getMessage());
            }
        } while (true);
    }

    private void purchaseCase(int entry) {
        switch (entry) {
            case 1:
                phoneCreditCase();
                break;
            case 2:
                System.out.println("Transaksi dibatalkan");
                break;
            default:
                System.out.println("Masukan salah.");
                break;
        }
    }

    private void phoneCreditCase() {
        int entry = 0;
        here:
        do {
            System.out.println("\n\nPembelian pulsa");
            System.out.println("1. Telkomsel");
            System.out.println("2. Batal");

            try {
                System.out.print("Entry: ");
                entry = Integer.parseInt(ClientHelper.read());

                switch (entry) {
                    case 1:
                        telkomselPhoneCredit();
                        break here;
                    case 2:
                        System.out.println("Transaksi dibatalkan");
                        break here;
                    default:
                        System.out.println("Masukan salah");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Phone credit case entry error: \n" + e.getMessage());
            }
        } while (true);
    }

    private void telkomselPhoneCredit() {
        int entry = 0;
        here:
        do {
            System.out.println("\n\nPilih pulsa");
            System.out.println("1. 10.000");
            System.out.println("2. 50.000");
            System.out.println("3. Batal");

            try {
                System.out.print("Masukkan pilihan:");
                entry = Integer.parseInt(ClientHelper.read());
                switch (entry) {
                    case 1:
                        processTelkomelCredit(10000);
                        break;
                    case 2:
                        processTelkomelCredit(50000);
                        break;
                    case 3:
                        System.out.println("Transaksi dibatalkan.");
                        break here;
                    default:
                        System.out.println("Masukan salah");
                        break;
                }
                break here;
            } catch (Exception e) {
                System.out.println("Telkomsel credit entry error:\n" + e.getMessage());
            }
        } while (true);
    }

    private void processTelkomelCredit(int amount) {
        System.out.print("\n\nNomor Telepon: ");
        String phoneNumber = ClientHelper.read();

        //informasi nomor telepon dikirim ke pihak ke tiga

        PhoneCreditPurchase phoneCreditPurchase = new PhoneCreditPurchase(phoneNumber, amount, "1131");
        String message = phoneCreditPurchase.purchaseInquiry(accountNumber, pinNumber);

        ISOMsg isoMessage = isoUtil.stringToISO(message);

        if (!isoMessage.getString(39).equals("00")) {
            System.out.println("Transaksi tidak dapat dilakukan.");
            return;
        }

        here:
        do {
            System.out.println("\n\nAnda akan melakukan pembayaran kepada:");
            System.out.println("Tujuan  : " + isoMessage.getString(102));
            System.out.println("Jumlah  : " + Integer.parseInt(isoMessage.getString(4)));
            System.out.println("Apakah anda ingin melanjutkan transaksi ?");
            System.out.println("1. Ya");
            System.out.println("2. Tidak");
            try {
                System.out.print("Entry: ");
                int entry = Integer.parseInt(ClientHelper.read());
                if (entry == 1) {
                    message = phoneCreditPurchase.purchase(message);
                    ISOMsg isoMsg = isoUtil.stringToISO(message);
                    if (isoMsg.getString(39).equals("00")) {
                        System.out.println("Transaksi berhasil");
                        break here;
                    } else {
                        System.out.println("Transaksi tidak dapat dilakukan");
                        break here;
                    }
                } else if (entry == 2) {
                    System.out.println("Transaksi dibatalkan");
                    break here;
                } else {
                    System.out.println("Masukan salah.");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } while (true);

//        System.out.println("\n\n\nPurchase Result");
//        System.out.println(message);
//        isoUtil.printISOMessage(isoUtil.stringToISO(message));
    }
}
