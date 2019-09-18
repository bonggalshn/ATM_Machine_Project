package com.bank.controller;

import com.bank.service.TransferExternalService;
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
    private TransferExternalService transferExternalService;

    public TransferController(TransferExternalService transferExternalService) {
        this.transferExternalService = transferExternalService;
    }

    @PostMapping(value = "/ReceiveExternalInquiry")
    public String receiveExternalInquiry(@RequestBody String message){
        return transferExternalService.receiveExternalInquiry(message);
    }

    @PostMapping(value = "/ReceiveExternalTransfer")
    public String receiveExternalTransfer(@RequestBody String message){
        String response = transferExternalService.receiveExternalTransfer(message);
        return response;
    }
}
