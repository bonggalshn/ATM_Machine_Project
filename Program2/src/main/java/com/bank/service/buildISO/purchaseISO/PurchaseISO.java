package com.bank.service.buildISO.purchaseISO;

import com.bank.client.interfaceClient.Purchase;
import com.bank.entity.Customer;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PurchaseISO {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseISO.class);
    public String phoneCreditInquiryISOResponse(ISOMsg isoMessage, Customer customer, Customer beneficiary, boolean status) {
        if (beneficiary.getAccountName().length() > 20)
            beneficiary.setAccountName(beneficiary.getAccountName().substring(0, 20));
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(2, customer.getAccountNumber());
            isoMsg.set(3, "381000");
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
            if (status)
                isoMsg.set(39, "00");
            else
                isoMsg.set(39, "51");
            isoMsg.set(41, "12340001");
            isoMsg.set(42, "000000000000000");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "840");
            isoMsg.set(54, isoMessage.getString(54));
            isoMsg.set(62, beneficiary.getAccountNumber());
            isoMsg.set(63, "0");
            isoMsg.set(102, beneficiary.getAccountName());

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String phoneCreditISOResponse(ISOMsg isoMessage, boolean status) {
        if (isoMessage.getString(102).length() > 20) {
            String companyName = isoMessage.getString(102).substring(0, 20);
        }

        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "181000");
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
            if (status == true)
                isoMsg.set(39, "00");
            else
                isoMsg.set(39, "05");
            isoMsg.set(41, "12340001");
            isoMsg.set(42, "000000000000000");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "840");
            isoMsg.set(54, isoMessage.getString(54));
            isoMsg.set(62, isoMessage.getString(62));
            isoMsg.set(63, "0");
            isoMsg.set(102, isoMessage.getString(102));

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
