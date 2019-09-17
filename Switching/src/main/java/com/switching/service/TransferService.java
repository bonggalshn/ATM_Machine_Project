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
        try {

            String bankCode = isoMessage.getString(127);


            String url = bankMappingService.getUrlInquiry(bankCode);

            if(!url.equals("404")){
                message = bankMappingService.setCharge(bankCode, isoMessage);

                return CommonUtil.sendData(message, url);
            }else {
                isoMessage.set(39,"05");
                byte[] result = isoMessage.pack();
                return new String(result);
            }
        }catch (Exception e){
            return "05";
        }
    }

    public String processTransfer(String message){
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String bankCode = isoMessage.getString(127);
        String url = bankMappingService.getUrlTransfer(bankCode);

        String response = CommonUtil.sendData(message, url);
        return response;
    }
}
