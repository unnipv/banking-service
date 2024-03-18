package com.example.BankingService.controller;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.User;
import com.example.BankingService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create user: " + e.getMessage());
        }
    }

    @GetMapping("/{username}/accounts")
    public ResponseEntity<?> getUserAccounts(@PathVariable String username) {
        try {
            List<Account> userAccounts = userService.getUserAccounts(username);
            return ResponseEntity.ok(userAccounts);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found: " + e.getMessage());
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // Return an appropriate error message and status code
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve user accounts: " + e.getMessage());
        }
    }

}