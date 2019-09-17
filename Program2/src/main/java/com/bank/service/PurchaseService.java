package com.bank.service;

import com.bank.Util.ISOUtil;
import com.bank.client.interfaceClient.Purchase;
import com.bank.entity.Customer;
import com.bank.service.buildISO.purchaseISO.PurchaseISO;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);
    private PurchaseISO purchaseISO = new PurchaseISO();
    private AccountService accountService;
    private ISOUtil isoUtil = new ISOUtil();

    @Autowired
    public PurchaseService(AccountService accountService) {
        this.accountService = accountService;
    }

    public String purchaseInquiry(String message) {
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String productCode = isoMessage.getString(32);
        String accountNumber = isoMessage.getString(2);
        String pinNumber = isoMessage.getString(52);
        int amount = Integer.parseInt(isoMessage.getString(4));

        boolean status = false;
        Customer customer = new Customer();
        if (accountService.checkAccount(accountNumber, pinNumber)) {
            customer = accountService.findByAccountNumber(accountNumber);
            if (customer.getBalance() > amount) {
                status = true;
            }
        }

        Customer beneficiary = accountService.findByAccountNumber(institutionCode(productCode));
        if (beneficiary == null) {
            beneficiary = new Customer();
            status = false;
        }

        String response = purchaseISO.phoneCreditInquiryISOResponse(isoMessage, customer, beneficiary, status);
        return response;

    }

    public String purchaseProcess(String message){
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String accountNumber = isoMessage.getString(2);
        String beneficiaryNumber = isoMessage.getString(62);
        int amount = Integer.parseInt(isoMessage.getString(4));

        boolean status = false;
        try{
            Customer customer = accountService.findByAccountNumber(accountNumber);
            Customer beneficiary = accountService.findByAccountNumber(beneficiaryNumber);

//            System.out.println(customer);
//            System.out.println(beneficiary);

            customer.setBalance(customer.getBalance()-amount);
            beneficiary.setBalance(beneficiary.getBalance()+amount);

            accountService.update(customer);
            accountService.update(beneficiary);

            status = true;
        }catch (Exception e){
            logger.error(e.getMessage());
            status = false;
        }

        System.out.println("\n\nSTATUS: "+status);
        String response = purchaseISO.phoneCreditISOResponse(isoMessage, status);
        logger.info("Purchasing performed for Account: '{}' to '{}'; Amount: '{}';",
                isoMessage.getString(2),
                isoMessage.getString(102),
                isoMessage.getString(4));
        return response;
    }

    private String institutionCode(String productCode) {
        String institutionNumber = "0";
        switch (productCode) {
            case "1131":
                institutionNumber = "1234567890";
                break;
            default:
                break;
        }
        return institutionNumber;
    }

}
