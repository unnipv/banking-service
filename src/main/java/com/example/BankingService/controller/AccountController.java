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

/**
 * This class acts as a controller for handling account-related operations in a banking service application.
 * It exposes various API endpoints for CRUD (Create, Read, Update, Delete) functionalities on accounts.
 *
 * @author Unni
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200") // Enables Cross-Origin Resource Sharing (CORS) for requests from the specified origin
@RequestMapping("/api/accounts") // Base path for all account-related endpoints
public class AccountController {

    /**
     * Injected AccountService dependency to interact with account-related business logic.
     */
    @Autowired
    private AccountService accountService;

    /**
     * Creates a new account based on the provided JSON data in the request body.
     *
     * @param accountJson A JSON string containing account details (username, branch, account type, balance)
     * @return A ResponseEntity object containing the newly created Account object and a CREATED status code (201) upon success.
     * @throws JsonProcessingException If there's an error processing the JSON request body.
     */
    @PostMapping("/create") // Endpoint for creating a new account
    public ResponseEntity<Account> createAccount(@RequestBody String accountJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> accountJsonData = mapper.readValue(accountJson, Map.class);

        String username = (String) accountJsonData.get("username");
        String branch = (String) accountJsonData.get("branch");
        String accountType = (String) accountJsonData.get("accountType");
        String balanceStr = (String) accountJsonData.get("balance");
        double balance = Double.parseDouble(balanceStr);

        Account createdAccount = accountService.createAccount(branch, accountType, username, balance);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    /**
     * Retrieves a list of transactions for a given account identified by its account number.
     *
     * @param accountNumber The unique identifier of the account to retrieve transactions for.
     * @return A ResponseEntity object containing a list of Transaction objects for the specified account and an OK status code (200) upon success.
     */
    @GetMapping("/{accountNumber}/transactions") // Endpoint for getting transactions for an account
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable String accountNumber) {
        List<Transaction> transactions = accountService.getTransactionHistory(accountNumber);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    /**
     * Retrieves the current balance of an account identified by its account number.
     *
     * @param accountNumber The unique identifier of the account to retrieve the balance for.
     * @return A ResponseEntity object containing the account's balance as a double value and an OK status code (200) upon success.
     */
    @GetMapping("/{accountNumber}/balance") // Endpoint for getting account balance
    public ResponseEntity<Double> getAccountBalance(@PathVariable String accountNumber) {
        double balance = accountService.getAccountBalance(accountNumber);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    /**
     * Transfers money between two accounts based on the provided JSON data in the request body.
     *
     * @param json A JSON string containing transfer details (from account, to account, amount, description)
     * @return A ResponseEntity object with appropriate status code depending on the transfer outcome:
     *          - OK (200) for successful transfer
     *          - PRECONDITION_FAILED (412) if there are insufficient funds in the source account
     *          - BAD_REQUEST (400) for any other error during transfer
     * @throws JsonProcessingException If there's an error processing the JSON request body.
     */
    @PostMapping("/transfer") // Endpoint for transferring money between accounts
    public ResponseEntity<?> transferMoney(@RequestBody String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> transferRequest = mapper.readValue(json, Map.class);
        String fromAccount = (String) transferRequest.get("fromAccount");
        String toAccount = (String) transferRequest.get("toAccount");
        String amountStr = (String) transferRequest.get("amount");
        double amount = Double.parseDouble(amountStr);
        String description = (String) transferRequest.get("description");

        Integer res = accountService.transferMoney(fromAccount, toAccount, amount, description);
        if (res == 0)
            return new ResponseEntity<>(HttpStatus.OK);
        else if (res == 2)
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
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
