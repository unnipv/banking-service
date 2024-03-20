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

/**
 * This service class implements the AccountService interface, providing core account-related functionalities.
 *
 * @author [Your Name Here] (replace with your name)
 */
@Service
@Transactional // Ensures transactional behavior for database operations
public class AccountServiceImpl implements AccountService {

    // Constants for branch and account type codes
    public static final Map<String, String> BRANCH_CODES = Map.of("Bengaluru", "1", "Chennai", "2", "Mumbai", "3");
    public static final Map<String, String> ACC_TYPE_CODES = Map.of("Savings", "1", "Current", "2");

    // Injected repositories for interacting with accounts and transactions
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Creates a new account with the provided details.
     *
     * @param branch      The branch name for the account
     * @param accountType The type of account (Savings or Current)
     * @param username    The username associated with the account
     * @param balance     The initial balance of the account
     * @return The newly created Account object
     */
    @Override
    public Account createAccount(String branch, String accountType, String username, Double balance) {
        Account account = new Account();

        // Set the account details
        account.setBalance(balance);
        account.setUsername(username);
        account.setAccountType(accountType);
        account.setBranch(branch);

        // Save the account to generate an ID
        account = accountRepository.save(account);

        // Generate account number and set the value
        String accountNumber = generateAccountNumber(branch, accountType, account.getId());
        account.setAccountNumber(accountNumber);

        // Save the account with updated details
        return accountRepository.save(account);
    }

    /**
     * Closes an account with the given account number.
     *
     * @param accountNumber The account number to be closed
     */
    @Override
    public void closeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with number: " + accountNumber);
        }
        accountRepository.delete(account);
    }

    /**
     * Transfers money between two accounts.
     *
     * @param fromAccountNumber The account number to transfer money from
     * @param toAccountNumber   The account number to transfer money to
     * @param amount            The amount to be transferred
     * @param description       A description of the transaction
     * @return 0 on successful transfer, 1 if either account is not found, 2 if insufficient funds
     */
    @Override
    public Integer transferMoney(String fromAccountNumber, String toAccountNumber, double amount, String description) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            return 1; // Indicate account not found
        }

        if (fromAccount.getBalance() < amount) {
            return 2; // Indicate insufficient funds
        }

        // Perform the transfer
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        // Create a transaction record
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccountNumber);
        transaction.setToAccount(toAccountNumber);
        transaction.setAmount(amount);
        transaction.setTransactionType("Transfer");
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());

        // Update accounts and save the transaction
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transactionRepository.save(transaction);

        return 0; // Indicate successful transfer
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
