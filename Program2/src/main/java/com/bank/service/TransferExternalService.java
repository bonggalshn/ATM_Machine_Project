package com.bank.service;

import com.bank.Util.ISOUtil;
import com.bank.client.ClientHelper;
import com.bank.entity.Customer;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TransferExternalService {
    private static final Logger logger = LoggerFactory.getLogger(TransferExternalService.class);
    private AccountService accountService;
    private ISOUtil isoUtil = new ISOUtil();

    public TransferExternalService(AccountService accountService) {
        this.accountService = accountService;
    }

    public String processExternalInquiry(String message) {

        ISOMsg isoMessage = isoUtil.stringToISO(message);
        String accountNumber = isoMessage.getString(2);
        String pinNumber = isoMessage.getString(52);

        String externalResponse = "";
        externalResponse = ClientHelper.sendData(message, "http://localhost:8082/switching/transferInquiry/transfer");
        if (externalResponse.equals("05")) {
            return "05";
        }
        isoMessage = isoUtil.stringToISO(externalResponse);

        if (!isoMessage.getString(39).equals("00")) {
            return externalResponse;
        }

        int amount = Integer.parseInt(isoMessage.getString(4));

        String status = "05";
        Customer issuer;
        if (accountService.checkAccount(accountNumber, pinNumber)) {
            issuer = accountService.findByAccountNumber(accountNumber);
            if (issuer.getBalance() >= amount) {
                status = "00";
            } else {
                status = "51";
            }
        }

        String response = buildISOExternalInquiryResponse(isoMessage, status);

        logger.info("Transfer inquiry created for Account: '{}' to Beneficiary: '{}' (Bank code: '{}'); Amount: '{}';",
                isoMessage.getString(2),
                isoMessage.getString(102),
                isoMessage.getString(127),
                isoMessage.getString(4));
//        System.out.println("created response: "+response);
        return response;
    }

    private String buildISOExternalInquiryResponse(ISOMsg isoMessage, String status) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "391000");
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
            isoMsg.set(100, "001");
            isoMsg.set(102, isoMessage.getString(102));//number
            isoMsg.set(103, isoMessage.getString(103));//name
            isoMsg.set(125, "0");
            isoMsg.set(127, isoMessage.getString(127));

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return e.getMessage();
        }
    }

    public String processExternalTransfer(String message) {
        String externalResponse = ClientHelper.sendData(message, "http://localhost:8082/switching/transfer");
        ISOMsg isoResponse = isoUtil.stringToISO(externalResponse);

//        System.out.println("Sout From external");
//        isoUtil.printISOMessage(isoResponse);

        if (!isoResponse.getString(39).equals("00")) {
            return externalResponse;
        }

        ISOMsg isoMessage = isoUtil.stringToISO(message);

//        isoUtil.printISOMessage(isoMessage);

        String status = "05";
        try {
            Customer issuer = accountService.findByAccountNumber(isoMessage.getString(2));
            issuer.setBalance(issuer.getBalance() - Integer.parseInt(isoMessage.getString(4)));
//            System.out.println(issuer);
            accountService.update(issuer);
            status = "00";
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        String response = buildISOTransferResponse(isoMessage, status);

        logger.info("Transfer performed for Account: '{}' to Beneficiary: '{}' (Bank code: '{}'); Amount: '{}';",
                isoMessage.getString(2),
                isoMessage.getString(102),
                isoMessage.getString(127),
                isoMessage.getString(4));
//        System.out.println("0012323: "+response);
        return response;
    }

    private String buildISOTransferResponse(ISOMsg isoMessage, String status) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "401000");
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
            isoMsg.set(100, "001");
            isoMsg.set(102, isoMessage.getString(102));
            isoMsg.set(103, isoMessage.getString(103));
            isoMsg.set(125, "0");
            isoMsg.set(127, isoMessage.getString(127));

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
