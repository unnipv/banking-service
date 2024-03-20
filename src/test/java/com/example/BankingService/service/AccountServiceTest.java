package com.example.BankingService.service;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.Transaction;
import com.example.BankingService.repository.AccountRepository;
import com.example.BankingService.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    public void testCloseAccount_Success() {
        Account account = new Account();
        account.setAccountNumber("123456");

        when(accountRepository.findByAccountNumber("123456")).thenReturn(account);

        accountService.closeAccount("123456");

        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    public void testCloseAccount_NotFound() {
        when(accountRepository.findByAccountNumber("123456")).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> accountService.closeAccount("123456"));
    }

    @Test
    public void testTransferMoney_Success() {
        Account fromAccount = new Account();
        fromAccount.setAccountNumber("123456");
        fromAccount.setBalance(500.0);

        Account toAccount = new Account();
        toAccount.setAccountNumber("654321");
        toAccount.setBalance(100.0);

        double transferAmount = 100.0;
        String description = "Test Transfer";

        when(accountRepository.findByAccountNumber(fromAccount.getAccountNumber())).thenReturn(fromAccount);
        when(accountRepository.findByAccountNumber(toAccount.getAccountNumber())).thenReturn(toAccount);

        int transferStatus = accountService.transferMoney(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), transferAmount, description);

        assertEquals(0, transferStatus);
        assertEquals(400.0, accountRepository.findByAccountNumber(fromAccount.getAccountNumber()).getBalance(), 0.001);
        assertEquals(200.0, accountRepository.findByAccountNumber(toAccount.getAccountNumber()).getBalance(), 0.001);

        // Verify interaction with repositories (optional)
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testTransferMoney_InsufficientFunds() {
        Account fromAccount = new Account();
        fromAccount.setAccountNumber("123456");
        fromAccount.setBalance(100.0);

        Account toAccount = new Account();
        toAccount.setAccountNumber("654321");
        toAccount.setBalance(0.0);

        double transferAmount = 200.0;
        String description = "Test Transfer";

        when(accountRepository.findByAccountNumber(fromAccount.getAccountNumber())).thenReturn(fromAccount);
        when(accountRepository.findByAccountNumber(toAccount.getAccountNumber())).thenReturn(toAccount);

        int transferStatus = accountService.transferMoney(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), transferAmount, description);

        assertEquals(2, transferStatus); // Code for insufficient funds

        // Verify no updates to balances
        assertEquals(100.0, accountRepository.findByAccountNumber(fromAccount.getAccountNumber()).getBalance(), 0.001);
        assertEquals(0.0, accountRepository.findByAccountNumber(toAccount.getAccountNumber()).getBalance(), 0.001);
    }
}

