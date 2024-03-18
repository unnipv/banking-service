package com.example.BankingService.repository;

import com.example.BankingService.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
    List<Account> findByUsername(String username);
}
