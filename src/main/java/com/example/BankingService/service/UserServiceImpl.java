package com.example.BankingService.service;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.User;
import com.example.BankingService.repository.AccountRepository;
import com.example.BankingService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Override
    public String createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            return "Username already exists.";
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        try{
            userRepository.save(user);
            return "Registration successful";
        } catch (Exception e){
            return "Registration failed.";
        }
    }
    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<Account> getUserAccounts(String username) {
        return accountRepository.findByUsername(username);
    }
}
