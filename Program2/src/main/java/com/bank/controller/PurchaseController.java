package com.bank.controller;

import com.bank.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("purchase")
public class PurchaseController {
    private PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/phoneCredit/inquiry")
    public String phoneCreditInquiry(@RequestBody String message){
        String response = purchaseService.purchaseInquiry(message);
        return response;
    }

    @PostMapping("/phoneCredit/process")
    public String phoneCreditPorcess(@RequestBody String message){
        String response = purchaseService.purchaseProcess(message);
        return response;
    }


}
