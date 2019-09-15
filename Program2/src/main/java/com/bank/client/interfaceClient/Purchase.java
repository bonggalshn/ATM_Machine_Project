package com.bank.client.interfaceClient;

public interface Purchase {
    String purchaseInquiry(String accountNumber, String pinNumber);
    String purchase(String message);
}
