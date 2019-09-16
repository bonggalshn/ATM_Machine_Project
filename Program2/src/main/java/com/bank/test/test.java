package com.bank.test;

import com.bank.client.ClientHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);
        test a = new test();
        here:
        do {
            try {
                System.out.print("Entry: ");
                int ans = Integer.parseInt(read.nextLine());

                switch (ans) {
                    case 1:
                        System.out.print("Message: ");
                        String msg = read.nextLine();
                        a.sendToExchange(msg);
                        break;
                    case 2:
                        a.getFromQueue();
                        break;
                    default:
                        break here;
                }
            } catch (Exception e) {
                System.out.println("Error input");
            }
        } while (true);

    }

    public void sendToExchange(String message) {

        String json = "{\n" +
                "\t\"properties\":{},\n" +
                "\t\"routing_key\":\"main_route\",\n" +
                "\t\"payload\":\"" + message + "\",\n" +
                "\t\"payload_encoding\":\"string\"\n" +
                "}";

        String url = "http://user01:user01@localhost:15672/api/exchanges/vhost01/mainExchange/publish";
        String response = ClientHelper.sendData(json, url);

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            boolean status = (boolean) jsonObject.get("routed");
            if (status) {
                System.out.println("Message sent");
            } else
                System.out.println("Cannot send the message");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public void getFromQueue() {
        String jsonString = "{\n" +
                "\t\"count\":1,\n" +
                "\t\"requeue\":true,\n" +
                "\t\"encoding\":\"auto\",\n" +
                "\t\"ackmode\":\"ack_requeue_false\"\n" +
                "}";

        String url = "http://user01:user01@localhost:15672/api/queues/vhost01/mainQueue/get";
        String response = ClientHelper.sendData(jsonString, url);
        JSONArray json;
        try {
            json = encode(response);
            System.out.println(((JSONObject) json.get(0)).get("payload"));
        } catch (Exception e) {
        }
    }

    public JSONArray encode(String jsonString) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray obj = (JSONArray) parser.parse(jsonString);
        return obj;
    }
}
