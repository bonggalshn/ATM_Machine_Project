package com.bank.service;

import com.bank.Util.ISOUtil;
import com.bank.client.interfaceClient.Payment;
import com.bank.entity.Customer;
import com.bank.service.buildISO.paymentISO.PaymentISO;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private AccountService accountService;
    private ISOUtil isoUtil = new ISOUtil();
    private PaymentISO paymentISO = new PaymentISO();

    public PaymentService(AccountService accountService) {
        this.accountService = accountService;
    }

    public String processInternetInquiry(String message) {

        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String accountNumber = isoMessage.getString(2);
        String pinNumber = isoMessage.getString(52);
        String internetCompanyNumber = isoMessage.getString(102);
        int amount = Integer.parseInt(isoMessage.getString(4));

        boolean status = false;
        Customer company = new Customer();
        Customer customer;
        if (accountService.checkAccount(accountNumber, pinNumber)) {
            company = accountService.findByAccountNumber(internetCompanyNumber);
            customer = accountService.findByAccountNumber(accountNumber);
            if (customer.getBalance() > amount) {
                status = true;
            }
        }

        String response = paymentISO.internetPaymentInquiryResponse(isoMessage, company.getAccountName(), status);
        return response;
    }

    public String processInternet(String message) {
//        System.out.println("processInternet" + message);
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String accountNumber = isoMessage.getString(2);
        String internetCompanyNumber = isoMessage.getString(102);
        int amount = Integer.parseInt(isoMessage.getString(4));

        Customer company = accountService.findByAccountNumber(internetCompanyNumber);
        Customer customer = accountService.findByAccountNumber(accountNumber);

        boolean status = false;
        try {
            if (company != null) {
                customer.setBalance(customer.getBalance() - amount);
                company.setBalance(company.getBalance() + amount);

                accountService.update(customer);
                accountService.update(company);
                status = true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            status = false;
        }


        String response = paymentISO.internetPaymentResponse(isoMessage, company.getAccountName(), status);
//        System.out.println("Payment service: " + response);
        logger.info("Payment perfomed for Account: '{}' to '{}' (Amount: '{}');",
                isoMessage.getString(2),
                isoMessage.getString(102),
                isoMessage.getString(4));
        return response;
    }
}
