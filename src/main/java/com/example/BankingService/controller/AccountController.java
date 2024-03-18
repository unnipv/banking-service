package com.example.BankingService.controller;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.Transaction;
import com.example.BankingService.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody String accountJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> accountJsonData = mapper.readValue(accountJson, Map.class);

        String username = (String) accountJsonData.get("username");
        String branch = (String) accountJsonData.get("branch");
        String accountType = (String) accountJsonData.get("accountType");
        Double balance = (Double) accountJsonData.get("balance");
        Account createdAccount = accountService.createAccount(branch, accountType, username, balance);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable String accountNumber) {
        List<Transaction> transactions = accountService.getTransactionHistory(accountNumber);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<Double> getAccountBalance(@PathVariable String accountNumber) {
        double balance = accountService.getAccountBalance(accountNumber);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody String json) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> transferRequest = mapper.readValue(json, Map.class);
        String fromAccount = (String) transferRequest.get("fromAccount");
        String toAccount = (String) transferRequest.get("toAccount");
        Double amount = (Double) transferRequest.get("amount");
        Integer res = accountService.transferMoney(fromAccount, toAccount, amount);
        if (res == 0)
            return new ResponseEntity<>("Transfer successful", HttpStatus.OK);
        else if (res == 1)
            return new ResponseEntity<>("Transfer unsuccessful. One or both accounts not found", HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>("Transfer unsuccessful. Insufficient funds", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<String> closeAccount(@PathVariable String accountNumber) {
        // Implement account closure logic here
        return new ResponseEntity<>("Account closed successfully", HttpStatus.OK);
    }
}
