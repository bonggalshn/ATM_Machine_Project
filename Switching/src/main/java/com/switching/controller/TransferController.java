package com.switching.controller;

import com.switching.Util.ISOUtil;
import com.switching.service.TransferService;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("switching")
public class TransferController {
    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    private ISOUtil isoUtil = new ISOUtil();
    private TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping(value = "/transferInquiry/transfer")
    public String transferInquiry(@RequestBody String message){
        return transferService.processTransferInquiry(message);
    }

    @PostMapping(value = "/transfer")
    public String transfer(@RequestBody String message){
        ISOMsg isoMsg = isoUtil.stringToISO(message);
        logger.info("Transfer to {} by Account '{}'",isoMsg.getString(127),isoMsg.getString(2));
        return transferService.processTransfer(message);
    }
}
