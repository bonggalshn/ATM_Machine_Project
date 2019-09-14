package com.bank.controller;

import com.bank.service.TransferExternalService;
import com.bank.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transfer")
public class TransferController {
    private TransferService transferService;
    private TransferExternalService transferExternalService;

    @Autowired
    public TransferController(TransferService transferService, TransferExternalService transferExternalService) {
        this.transferService = transferService;
        this.transferExternalService = transferExternalService;
    }

    @PostMapping(value = "/internalInquiry", produces = "application/json; charset=UTF-8")
    public String transferInternalInquiry(@RequestBody String message) {
//        System.out.println(message);
        return transferService.processInquiry(message);
    }

    @PostMapping(value = "/internal", produces = "application/json; charset=UTF-8")
    public String transferInternal(@RequestBody String message) {
        return transferService.process(message);
    }

    @PostMapping(value = "/externalInquiry", produces = "application/json; charset=UTF-8")
    public String transferExternalInquiry(@RequestBody String message) {
        String response =  transferExternalService.processExternalInquiry(message);
        return response;
    }

    @PostMapping(value = "/external", produces = "application/json; charset=UTF-8")
    public String transferExternal(@RequestBody String message) {
        String response =  transferExternalService.processExternalTransfer(message);
        return response;
    }
}
