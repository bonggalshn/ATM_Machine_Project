package com.bank.controller;

import com.bank.service.TransferExternalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transfer")
public class TransferController {
    private TransferExternalService transferExternalService;

    public TransferController(TransferExternalService transferExternalService) {
        this.transferExternalService = transferExternalService;
    }

    @PostMapping(value = "/ReceiveExternalInquiry")
    public String receiveExternalInquiry(@RequestBody String message){
        System.out.println("Processing external transfer inquiry");
        String response = transferExternalService.receiveExternalInquiry(message);
        System.out.println("response: "+response);
        return response;
    }

    @PostMapping(value = "/ReceiveExternalTransfer")
    public String receiveExternalTransfer(@RequestBody String message){
        String response = transferExternalService.receiveExternalTransfer(message);
        return response;
    }

    @PostMapping(value = "/test")
    public String test(@RequestBody String message){
        System.out.println("Test");
        return message;
    }
}
