package com.bank.test;

import com.bank.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class test3 {
    private static final Logger logger = LoggerFactory.getLogger(test3.class);

    public static void main(String[] args) {
        int x=0,y=0;
        logger.info("test {} {}",x,y);

//        test t = new test();
//        test.main(new String[0]);
        Client atm = new Client("localhost",8089);
        atm.main(new String[0]);

    }


}
