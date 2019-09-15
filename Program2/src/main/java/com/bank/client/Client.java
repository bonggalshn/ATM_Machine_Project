package com.bank.client;

public class Client {
    private static ClientHelper helper = new ClientHelper();

    public static void main(String[] args) {
        int entry = 0;
        do {
            if (!helper.showMainMenu())
                break;
            try {
                System.out.print("Entry: ");
                entry = Integer.parseInt(ClientHelper.read());
                helper.processMenu(entry);
            } catch (Exception e) {
                System.out.println("Masukan salah.");
                System.out.println("Main client: "+e.getMessage());
            }
        } while (entry != 6);
    }
}
