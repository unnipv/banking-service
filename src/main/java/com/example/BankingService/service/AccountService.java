package com.example.BankingService.service;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.Transaction;

import java.util.List;

public interface AccountService {
    Account createAccount(String branch, String accountType, String name, Double balance);

    void closeAccount(String accountNumber);
    Integer transferMoney(String fromAccountNumber, String toAccountNumber, double amount, String description);
    double getAccountBalance(String accountNumber);
    List<Transaction> getTransactionHistory(String accountNumber);
}
