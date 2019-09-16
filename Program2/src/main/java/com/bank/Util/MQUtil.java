package com.bank.Util;

import com.bank.client.ClientHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MQUtil {
    public void sendToExchange(String exchangeName, String message) {
        String json = "{\n" +
                "\t\"properties\":{},\n" +
                "\t\"routing_key\":\"main_route\",\n" +
                "\t\"payload\":\"" + message + "\",\n" +
                "\t\"payload_encoding\":\"string\"\n" +
                "}";

        String url = "http://user01:user01@localhost:15672/api/exchanges/vhost01/"+exchangeName+"/publish";
        String response = ClientHelper.sendData(json, url);

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            boolean status = (boolean) jsonObject.get("routed");
            if (status) {
//                System.out.println("Message sent");
            } else
                System.out.println("Cannot send the message");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public String getFromQueue(String queueName) {
        String jsonString = "{\n" +
                "\t\"count\":1,\n" +
                "\t\"requeue\":true,\n" +
                "\t\"encoding\":\"auto\",\n" +
                "\t\"ackmode\":\"ack_requeue_false\"\n" +
                "}";

        String url = "http://user01:user01@localhost:15672/api/queues/vhost01/"+queueName+"/get";
        String response = ClientHelper.sendData(jsonString, url);
        JSONArray json = new JSONArray();
        try {
            json = encode(response);
            return ((JSONObject) json.get(0)).get("payload")+"";
        } catch (Exception e) {
            return "";
        }
    }

    private JSONArray encode(String jsonString) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray obj = (JSONArray) parser.parse(jsonString);
        return obj;
    }
}
