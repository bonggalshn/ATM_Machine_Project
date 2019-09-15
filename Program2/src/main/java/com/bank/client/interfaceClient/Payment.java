package com.bank.client.interfaceClient;

public interface Payment {
    String payInquiry(String accountNumber, String pinNumber, int bill);
    String pay(String message);
}
