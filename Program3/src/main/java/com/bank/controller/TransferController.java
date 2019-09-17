package com.bank.controller;

import com.bank.Util.ISOUtil;
import com.bank.service.TransferExternalService;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transfer")
public class TransferController {
    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);
    ISOUtil isoUtil=new ISOUtil();
    private TransferExternalService transferExternalService;

    public TransferController(TransferExternalService transferExternalService) {
        this.transferExternalService = transferExternalService;
    }

    @PostMapping(value = "/ReceiveExternalInquiry")
    public String receiveExternalInquiry(@RequestBody String message){
        ISOMsg isoMsg = isoUtil.stringToISO(message);
        logger.info("Receive transfer inquiry from account '{}', amount: '{}'",isoMsg.getString(2),Integer.parseInt(isoMsg.getString(4)));
        return transferExternalService.receiveExternalInquiry(message);
    }

    @PostMapping(value = "/ReceiveExternalTransfer")
    public String receiveExternalTransfer(@RequestBody String message){
        ISOMsg isoMsg = isoUtil.stringToISO(message);
        String response = transferExternalService.receiveExternalTransfer(message);
        logger.info("Receive transfer from account '{}', amount: '{}'",isoMsg.getString(2), Integer.parseInt(isoMsg.getString(4)));
        return response;
    }
}
