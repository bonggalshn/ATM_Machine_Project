package com.bank.Util;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class ISOUtil {
    private static final Logger logger = LoggerFactory.getLogger(ISOUtil.class);
    public void printISOMessage(ISOMsg isoMsg) {
        try {
            System.out.printf("MTI = %s%n", isoMsg.getMTI());
            for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                if (isoMsg.hasField(i)) {
                    System.out.printf("Field (%s) = %s%n", i, isoMsg.getString(i));
                }
            }
        } catch (ISOException e) {
            logger.error(e.getMessage());
        }
    }

    public ISOMsg stringToISO(String isoMessage){
        ISOMsg isoMsg = new ISOMsg();

        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            // Setting packager
            isoMsg.setPackager(packager);

            byte[] bIsoMessage = new byte[isoMessage.length()];
            for (int i = 0; i < bIsoMessage.length; i++) {
                bIsoMessage[i] = (byte) (int) isoMessage.charAt(i);
            }

            isoMsg.unpack(bIsoMessage);

            return isoMsg;

        }catch (Exception e){
            logger.error(e.getMessage());
            return isoMsg;
        }
    }

    public boolean checkISO(String message){
        try{
            stringToISO(message);
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }
}
