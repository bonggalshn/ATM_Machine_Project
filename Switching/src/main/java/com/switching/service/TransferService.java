package com.switching.service;

import com.switching.Util.CommonUtil;
import com.switching.Util.ISOUtil;
import com.switching.controller.TransferController;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
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

            if (!url.equals("404")) {
                message = bankMappingService.setChargeInquiry(bankCode, isoMessage);

                return CommonUtil.sendData(message, url);
            } else {
                isoMessage.set(39, "05");
                byte[] result = isoMessage.pack();
                return new String(result);
            }
        } catch (Exception e) {
            return "05";
        }
    }

    public String processTransfer(String message) {
        ISOMsg isoMessage = isoUtil.stringToISO(message);

        String bankCode = isoMessage.getString(127);
        String url = bankMappingService.getUrlTransfer(bankCode);

        message = bankMappingService.setChargeTransfer(bankCode, isoMessage);

        String response = CommonUtil.sendData(message, url);
        logger.info("Transfer to '{}-{}' from Account '{}'",
                isoMessage.getString(127),
                isoMessage.getString(102),
                isoMessage.getString(2));
        return response;
    }
}
