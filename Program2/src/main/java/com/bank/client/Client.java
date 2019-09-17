package com.bank.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
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
        logger.info("ATM {}:{} used.",
                Client.getServer(),
                Client.getPort());

        int entry = 0;
        do {
            if (!helper.showMainMenu())
                break;
            try {
                System.out.print("Entry: ");
                entry = Integer.parseInt(ClientHelper.read());
                helper.processMenu(entry);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } while (true);
    }
}
