package com.switching.controller;

import com.switching.service.TransferService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("switching")
public class TransferController {
    private TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping(value = "/transferInquiry/transfer")
    public String transferInquiry(@RequestBody String message){
        System.out.println("Process the inquiry");
        String response = transferService.processTransferInquiry(message);
        System.out.println(response);
        return response;
    }

    @PostMapping(value = "/transfer")
    public String transfer(@RequestBody String message){
        return transferService.processTransfer(message);
    }

    @PostMapping(value = "/test")
    public String test(@RequestBody String message){
        return message;
    }
}
