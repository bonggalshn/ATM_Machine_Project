package com.bank.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class ClientHelper {
    private static final Logger logger = LoggerFactory.getLogger(ClientHelper.class);
    private String accountNumber;
    private String pinNumber;

    boolean showMainMenu() {
        try {
            System.out.println("\n\n-----------------------");
            System.out.print("Account Number: ");
            this.accountNumber = read();
            System.out.print("Pin Number    : ");
            this.pinNumber = read();
            System.out.println("-----------------------");

            System.out.println("--ATM MACHINE----------");
            System.out.println("1. Tarik Tunai    4. Info Saldo");
            System.out.println("2. Transfer       5. Pembayaran");
            System.out.println("3. Pembelian      6. Keluar");
            System.out.println("-----------------------");

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    void processMenu(int entry) {
        switch (entry) {
            case 1:
                WithdrawalHelper withdrawal = new WithdrawalHelper();
                withdrawal.withdrawalMain(accountNumber, pinNumber);
                break;

            case 2:
                TransferHelper transferHelper = new TransferHelper();
                transferHelper.TransferMain(accountNumber, pinNumber);
                break;

            case 3:
                PurchaseHelper purchaseHelper = new PurchaseHelper(accountNumber, pinNumber);
                purchaseHelper.purchaseMain();
                break;

            case 4:
                BalanceHelper balance = new BalanceHelper();
                balance.inquiry(accountNumber, pinNumber);
                break;

            case 5:
                PaymentHelper paymentHelper = new PaymentHelper();
                paymentHelper.PaymentMain(accountNumber, pinNumber);
                break;

            case 6:
                System.out.println("Keluar");
                break;

            default:
                System.out.println("Masukan salah.");
                break;
        }
    }

    public static String read() {
        Scanner scan = new Scanner(System.in);
        String result = scan.nextLine();
        return result;
    }

    public static String sendData(String data, String uri) {
        try {
            CloseableHttpClient client = HttpClients.createSystem();
            HttpPost httpPost = new HttpPost(uri);

            StringEntity entity = new StringEntity(data);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            client.close();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
