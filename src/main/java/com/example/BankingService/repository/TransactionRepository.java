package com.example.BankingService.repository;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountOrToAccountOrderByTimestampDesc(String fromAccountNumber, String toAccountNumber);
}
