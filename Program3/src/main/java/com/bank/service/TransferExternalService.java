package com.bank.service;

import com.bank.Util.ISOUtil;
import com.bank.entity.Customer;
import com.bank.service.ISOBuilder.transfer.ExternalTransferISOBuilder;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferExternalService {
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

        System.out.println("Finding beneficiary");
        Customer beneficiary = accountService.findByAccountNumber(beneficiaryNumber);

        boolean status = false;
        if (beneficiary != null)
            status = true;

        System.out.println("sending response");
        String response = ISOBuilder.ExternalInquiryISOresponse(isoMessage, beneficiary, status, amount);

        return response;
    }

    public String receiveExternalTransfer(String message){

        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String beneficiaryNumber = isoMessage.getString(102);
        int amount = Integer.parseInt(isoMessage.getString(4));

        boolean status = false;
        try {
            Customer beneficiary = accountService.findByAccountNumber(beneficiaryNumber);
            beneficiary.setBalance(beneficiary.getBalance()+amount);
            accountService.update(beneficiary);
            status = true;
        }catch (Exception e){
            System.out.println("Receiving process: "+e.getMessage());
            status = false;
        }

        String response= ISOBuilder.ExternalTransferISOresponse(isoMessage,status);

        isoUtil.printISOMessage(isoUtil.stringToISO(response));

        return response;
    }
}
