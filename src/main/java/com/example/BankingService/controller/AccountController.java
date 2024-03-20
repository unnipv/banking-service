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
@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<?> transferMoney(@RequestBody String json) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> transferRequest = mapper.readValue(json, Map.class);
        String fromAccount = (String) transferRequest.get("fromAccount");
        String toAccount = (String) transferRequest.get("toAccount");
        Double amount = (Double) transferRequest.get("amount");
        String description = (String) transferRequest.get("description");
        Integer res = accountService.transferMoney(fromAccount, toAccount, amount, description);
        if (res == 0)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{accountNumber}/close")
    public ResponseEntity<String> closeAccount(@PathVariable String accountNumber) {
        try {
            accountService.closeAccount(accountNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
