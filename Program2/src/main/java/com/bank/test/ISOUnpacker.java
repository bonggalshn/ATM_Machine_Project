package com.bank.test;

import com.bank.Util.ISOUtil;
import com.bank.client.ClientHelper;

public class ISOUnpacker {
    public static void main(String[] args) {
        do {
            ISOUtil isoUtil = new ISOUtil();
            System.out.println("String ISO: ");
            String isoMessage = ClientHelper.read();
            isoUtil.printISOMessage(isoUtil.stringToISO(isoMessage));
        }while (true);

    }
}
