package com.bank.service;

import com.bank.Util.ISOUtil;
import com.bank.entity.Customer;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    private AccountService accountService;
    private ISOUtil isoUtil = new ISOUtil();

    @Autowired
    public TransferService(AccountService accountService) {
        this.accountService = accountService;
    }

    public String processInquiry(String message) {
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String accountNumber = isoMessage.getString(2);
        String pinNumber = isoMessage.getString(52);
        int amount = Integer.parseInt(isoMessage.getString(4));
        String beneficiaryNumber = isoMessage.getString(102);

        String status = "05";
        Customer customer = null;
        Customer beneficiaryAccount = null;
        if (accountService.checkAccount(accountNumber, pinNumber)) {
            customer = accountService.findByAccountNumber(accountNumber);
            beneficiaryAccount = accountService.findByAccountNumber(beneficiaryNumber);
            if (customer != null && beneficiaryAccount != null) {
                if (customer.getBalance() >= amount) {
                    if (beneficiaryAccount.getAccountName().length() > 20)
                        beneficiaryAccount.setAccountName(beneficiaryAccount.getAccountName().substring(0, 20));
                    if (beneficiaryAccount.getAccountNumber().length() > 20)
                        beneficiaryAccount.setAccountNumber(beneficiaryAccount.getAccountNumber().substring(0, 20));
                    status = "00";
                }else{
                    status = "51";
                }
            }
        }

        String response = buildISOInquiry(isoMessage, customer, beneficiaryAccount, status);

//        System.out.println("ISO Message: " + isoMessage);
        logger.info("Transfer inquiry created for Account: '{}' to Beneficiary: '{}'; Amount: '{}';",
                isoMessage.getString(2),
                isoMessage.getString(103),
                isoMessage.getString(4));
        return response;
    }

    private String buildISOInquiry(ISOMsg isoMessage, Customer customer, Customer beneficiary, String status) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            if (customer == null)
                isoMsg.set(2, "0");
            else
                isoMsg.set(2, customer.getAccountNumber());

            isoMsg.set(3, "390000");
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
            isoMsg.set(100, isoMessage.getString(100));

            if (beneficiary == null) {
                isoMsg.set(102, "0");
                isoMsg.set(103, "0");
            } else {
                isoMsg.set(102, beneficiary.getAccountNumber());
                isoMsg.set(103, beneficiary.getAccountName());
            }

            isoMsg.set(125, "0");
            isoMsg.set(127, isoMessage.getString(127));

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return e.getMessage();
        }
    }

    public String process(String message) {
        // TO DO: return balance left
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String accountNumber = isoMessage.getString(2);
        int amount = Integer.parseInt(isoMessage.getString(4));
        String beneficiaryNumber = isoMessage.getString(102);

        Customer sender = accountService.findByAccountNumber(accountNumber);
        Customer beneficiary = accountService.findByAccountNumber(beneficiaryNumber);

        sender.setBalance(sender.getBalance() - amount);
        beneficiary.setBalance(beneficiary.getBalance() + amount);

        String status = "05";
        try {
            accountService.update(sender);
            accountService.update(beneficiary);
            status = "00";
        } catch (Exception e) {
            logger.error(e.getMessage());
        }


        logger.info("Transfer perform for Account: '{}' to Beneficiary: '{}'; Amount: '{}';",
                isoMessage.getString(2),
                isoMessage.getString(103),
                isoMessage.getString(4));

        return buildISOTransfer(isoMessage, beneficiary, status);

    }

    private String buildISOTransfer(ISOMsg isoMessage, Customer benefeiciary, String status) {
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "400000");
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
            if (benefeiciary == null) {
                isoMsg.set(102, "0");
                isoMsg.set(103, "0");
            } else {
                isoMsg.set(102, benefeiciary.getAccountNumber());
                isoMsg.set(103, benefeiciary.getAccountName());
            }
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
