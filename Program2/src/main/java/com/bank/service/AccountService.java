package com.bank.service;

import com.bank.entity.Customer;
import com.bank.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Customer findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Customer update(Customer customer) {
        return accountRepository.save(customer);
    }

    public boolean checkAccount(String accountNumber, String pinNumber) {
        Customer customer = findByAccountNumber(accountNumber);
        if (customer == null) {
            logger.info("Account '{}' is not found;", accountNumber);
            return false;
        }

        if (Integer.parseInt(customer.getPinNumber()) == Integer.parseInt(pinNumber)) {
            logger.info("Login success for account '{}';", accountNumber);
            return true;
        } else {
            logger.warn("Login failed for account '{}';", accountNumber);
            return false;
        }
    }
}
