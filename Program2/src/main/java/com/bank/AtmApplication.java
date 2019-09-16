package com.bank;

import com.bank.Util.MQUtil;
import com.bank.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AtmApplication {
    private static MQUtil mqUtil = new MQUtil();

    public static void main(String[] args) {
        SpringApplication.run(AtmApplication.class, args);
        System.out.println("READY...");
    }

}
