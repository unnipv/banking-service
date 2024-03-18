package com.example.BankingService.service;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    boolean checkUsernameExists(String username);
    List<Account> getUserAccounts(String username);
}
