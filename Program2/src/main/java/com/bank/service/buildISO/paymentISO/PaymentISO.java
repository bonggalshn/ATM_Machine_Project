package com.bank.service.buildISO.paymentISO;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentISO {
    private static final Logger logger = LoggerFactory.getLogger(PaymentISO.class);

    public String internetPaymentInquiryResponse(ISOMsg isoMessage, String internetCompanyName, String status) {
        try {
            if (internetCompanyName.length() > 20)
                internetCompanyName = internetCompanyName.substring(0, 20);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "380000");
            isoMsg.set(4, isoMessage.getString(4));
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "000001");
            isoMsg.set(12, new SimpleDateFormat("HHmmss").format(new Date()));
            isoMsg.set(13, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(15, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(18, "0000");
            isoMsg.set(32, "00000000000");
            isoMsg.set(33, "00000000000");
            isoMsg.set(37, "000000000000");
            isoMsg.set(39, status);
            isoMsg.set(41, "12340001");
            isoMsg.set(42, "000000000000000");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "840");
            isoMsg.set(54, isoMessage.getString(54));
            isoMsg.set(62, "0");
            isoMsg.set(63, "0");
            isoMsg.set(102, internetCompanyName);

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String internetPaymentResponse(ISOMsg isoMessage, String internetCompanyName, String status) {
        if (internetCompanyName.length() > 20)
            internetCompanyName = internetCompanyName.substring(0, 20);
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "180000");
            isoMsg.set(4, isoMessage.getString(4));
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "000001");
            isoMsg.set(12, new SimpleDateFormat("HHmmss").format(new Date()));
            isoMsg.set(13, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(15, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(18, "0000");
            isoMsg.set(32, "00000000000");
            isoMsg.set(33, "00000000000");
            isoMsg.set(37, "000000000000");
            isoMsg.set(39, status);
            isoMsg.set(41, "12340001");
            isoMsg.set(42, "000000000000000");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "840");
            isoMsg.set(54, isoMessage.getString(54));
            isoMsg.set(62, "0");
            isoMsg.set(63, "0");
            isoMsg.set(102, internetCompanyName);

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String rejectPayment() {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(39, "05");

            isoMsg.set(102, "0");

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
