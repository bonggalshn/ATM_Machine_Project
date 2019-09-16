package com.bank.client;

public class Client {
    private static ClientHelper helper = new ClientHelper();
    private static String server;
    private static int port;

    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public static String getServer(){
        return server;
    }

    public static String getPort(){
        return port+"";
    }

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
