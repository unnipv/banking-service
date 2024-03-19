package com.example.BankingService.service;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.Transaction;
import com.example.BankingService.repository.AccountRepository;
import com.example.BankingService.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AccountServiceImpl implements AccountService{
    //Constants
    public static final Map<String, String> BRANCH_CODES = Map.of("Bengaluru", "1", "Chennai", "2", "Mumbai", "3");
    public static final Map<String, String> ACC_TYPE_CODES = Map.of("Savings", "1", "Current", "2");
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Account createAccount(String branch, String accountType, String username, Double balance) {
        Account account = new Account();

        //Set the value of account details
        account.setBalance(balance);
        account.setUsername(username);
        account.setAccountType(accountType);
        account.setBranch(branch);

        // Save the account to generate ID
        account = accountRepository.save(account);

        // Generate account number and set the value
        String accountNumber = generateAccountNumber(branch, accountType, account.getId());
        account.setAccountNumber(accountNumber);

        // Save the account with updated details
        return accountRepository.save(account);
    }

    @Override
    public void closeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with number: " + accountNumber);
        }
        accountRepository.delete(account);
    }

    @Override
    public Integer transferMoney(String fromAccountNumber, String toAccountNumber, double amount, String description) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            return 1;
        }

        if (fromAccount.getBalance() < amount) {
            return 2;
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccountNumber);
        transaction.setToAccount(toAccountNumber);
        transaction.setAmount(amount);
        transaction.setTransactionType("Transfer");
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        // Update both accounts in the database
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transactionRepository.save(transaction);
        return 0;
    }

    @Override
    public double getAccountBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with number: " + accountNumber);
        }
        return account.getBalance();
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {

        }
        return transactionRepository.findByFromAccountOrToAccountOrderByTimestampDesc(accountNumber, accountNumber);
    }

    private String generateAccountNumber(String branch, String accountType, Long accountId) {
        String branchCode = BRANCH_CODES.get(branch);
        String accountTypeCode = ACC_TYPE_CODES.get(accountType);
        return branchCode + accountTypeCode + String.format("%08d", accountId);
    }
}
