package com.switching.service;

import com.switching.Util.CommonUtil;
import com.switching.Util.ISOUtil;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private BankMappingService bankMappingService;
    private ISOUtil isoUtil = new ISOUtil();

    @Autowired
    public TransferService(BankMappingService bankMappingService) {
        this.bankMappingService = bankMappingService;
    }

    public String processTransferInquiry(String message) {
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String bankCode = isoMessage.getString(127);

        String url = bankMappingService.getUrlInquiry(bankCode);
        message = bankMappingService.setCharge(bankCode, isoMessage);

        System.out.println("Send to beneficiary bank");
        String response = CommonUtil.sendData(message,url);
        System.out.println("response receive");

        isoUtil.printISOMessage(isoUtil.stringToISO(response));
        return response;
    }

    public String processTransfer(String message){
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        System.out.println("Transfer order");
        isoUtil.printISOMessage(isoMessage);

        String bankCode = isoMessage.getString(127);
        String url = bankMappingService.getUrlTransfer(bankCode);

        String response = CommonUtil.sendData(message, url);
        System.out.println("Transfer response");
        isoUtil.printISOMessage(isoUtil.stringToISO(response));
        return response;

    }
}
