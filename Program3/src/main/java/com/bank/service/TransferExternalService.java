package com.bank.service;

import com.bank.Util.ISOUtil;
import com.bank.controller.TransferController;
import com.bank.entity.Customer;
import com.bank.service.ISOBuilder.transfer.ExternalTransferISOBuilder;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferExternalService {
    private static final Logger logger = LoggerFactory.getLogger(TransferExternalService.class);
    private AccountService accountService;
    private ISOUtil isoUtil = new ISOUtil();
    private ExternalTransferISOBuilder ISOBuilder = new ExternalTransferISOBuilder();

    @Autowired
    public TransferExternalService(AccountService accountService) {
        this.accountService = accountService;
    }

    public String receiveExternalInquiry(String message) {

        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String beneficiaryNumber = isoMessage.getString(102);
        int amount = Integer.parseInt(isoMessage.getString(4));

        Customer beneficiary = accountService.findByAccountNumber(beneficiaryNumber);

        boolean status = false;
        if (beneficiary != null)
            status = true;

        return ISOBuilder.ExternalInquiryISOresponse(isoMessage, beneficiary, status, amount);
    }

    public String receiveExternalTransfer(String message) {

        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String beneficiaryNumber = isoMessage.getString(102);
        int amount = Integer.parseInt(isoMessage.getString(4));

        boolean status = false;
        try {
            Customer beneficiary = accountService.findByAccountNumber(beneficiaryNumber);
            beneficiary.setBalance(beneficiary.getBalance() + amount);
            accountService.update(beneficiary);
            status = true;
        } catch (Exception e) {
            logger.error("Method: receiveExternalTransfer, Error: {}", e.getMessage());
            status = false;
        }

        String response = ISOBuilder.ExternalTransferISOresponse(isoMessage, status);

        return response;
    }
}
