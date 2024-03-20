package com.example.BankingService.controller;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.Transaction;
import com.example.BankingService.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService mockAccountService;

    @Test
    public void testCreateAccount_Success() throws JsonProcessingException {
        String username = "testuser";
        String branch = "Bengaluru";
        String accountType = "Savings";
        Double balance = 1000.0;

        Map<String, Object> accountJsonData = new HashMap<>();
        accountJsonData.put("username", username);
        accountJsonData.put("branch", branch);
        accountJsonData.put("accountType", accountType);
        accountJsonData.put("balance", balance);

        ObjectMapper mapper = new ObjectMapper();
        String accountJson = mapper.writeValueAsString(accountJsonData);

        Account mockAccount = new Account();
        when(mockAccountService.createAccount(branch, accountType, username, balance)).thenReturn(mockAccount);

        ResponseEntity<Account> response = accountController.createAccount(accountJson);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockAccount, response.getBody());
    }

    @Test
    public void testCreateAccount_Failure_JsonProcessingException() throws JsonProcessingException {
        String invalidJson = "{ \"username\": \"invalid_json\" }";

        when(mockAccountService.createAccount(any(), any(), any(), any())).thenReturn(null); // Not called in this case

        ResponseEntity<?> response = accountController.createAccount(invalidJson);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testGetAccountTransactions_Success() {
        String accountNumber = "123456";
        List<Transaction> mockTransactions = new ArrayList<>();
        when(mockAccountService.getTransactionHistory(accountNumber)).thenReturn(mockTransactions);

        ResponseEntity<List<Transaction>> response = accountController.getAccountTransactions(accountNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTransactions, response.getBody());
    }

    @Test
    public void testGetAccountBalance_Success() {
        String accountNumber = "123456";
        double balance = 1000.0;
        when(mockAccountService.getAccountBalance(accountNumber)).thenReturn(balance);

        ResponseEntity<Double> response = accountController.getAccountBalance(accountNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balance, response.getBody());
    }

    @Test
    public void testTransferMoney_Success() throws JsonProcessingException {
        String fromAccount = "123456";
        String toAccount = "654321";
        double amount = 500.0;
        String description = "Test Transfer";

        Map<String, Object> transferRequest = new HashMap<>();
        transferRequest.put("fromAccount", fromAccount);
        transferRequest.put("toAccount", toAccount);
        transferRequest.put("amount", amount);
        transferRequest.put("description", description);

        ObjectMapper mapper = new ObjectMapper();
        String transferJson = mapper.writeValueAsString(transferRequest);

        when(mockAccountService.transferMoney(fromAccount, toAccount, amount, description)).thenReturn(0); // Successful transfer

        ResponseEntity<?> response = accountController.transferMoney(transferJson);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testTransferMoney_Failure() throws JsonProcessingException {
        String fromAccount = "123456";
        String toAccount = "654321";
        double amount = 500.0;
        String description = "Test Transfer";

        Map<String, Object> transferRequest = new HashMap<>();
        transferRequest.put("fromAccount", fromAccount);
        transferRequest.put("toAccount", toAccount);
        transferRequest.put("amount", amount);
        transferRequest.put("description", description);

        ObjectMapper mapper = new ObjectMapper();
        String transferJson = mapper.writeValueAsString(transferRequest);

        when(mockAccountService.transferMoney(fromAccount, toAccount, amount, description)).thenReturn(1); // Transfer failure

        ResponseEntity<?> response = accountController.transferMoney(transferJson);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCloseAccount_Success() {
        String accountNumber = "123456";

        doNothing().when(mockAccountService).closeAccount(accountNumber); // No exception thrown

        ResponseEntity<String> response = accountController.closeAccount(accountNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCloseAccount_Failure() {
        String accountNumber = "123456";

        doThrow(new RuntimeException("Account closure failed")).when(mockAccountService).closeAccount(accountNumber);

        ResponseEntity<String> response = accountController.closeAccount(accountNumber);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
