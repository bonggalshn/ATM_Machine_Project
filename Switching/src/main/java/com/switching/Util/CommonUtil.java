package com.switching.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Scanner;

public class CommonUtil {
    public static String read() {
        Scanner scan = new Scanner(System.in);
        String result = scan.nextLine();
        return result;
    }

    public static String sendData(String data, String uri) {
        try{
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
        }catch (Exception e){
            return "";
        }
    }
}
