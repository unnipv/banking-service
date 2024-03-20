package com.example.BankingService.controller;

import com.example.BankingService.model.Account;
import com.example.BankingService.model.User;
import com.example.BankingService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This controller handles user-related operations, providing endpoints for user registration and retrieval of accounts.
 *
 * @author Unni
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200") // Enables CORS for requests from the specified origin
@RequestMapping("/api/users") // Base path for user-related endpoints
public class UserController {

    /**
     * Injected UserService dependency to interact with user-related business logic.
     */
    @Autowired
    private UserService userService;

    /**
     * Registers a new user based on the provided User object in the request body.
     *
     * @param user The User object containing registration details
     * @return A ResponseEntity with appropriate status code:
     *          - CREATED (201) if registration is successful
     *          - PRECONDITION_FAILED (412) if registration fails for other reasons
     *          - INTERNAL_SERVER_ERROR (500) for unexpected errors
     */
    @PostMapping("/register") // Endpoint for user registration
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            String res = userService.createUser(user);
            Map<String, String> response = new HashMap<>();
            response.put("message", res);
            if (Objects.equals(res, "Registration successful")) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of accounts associated with a given username.
     *
     * @param username The username for which to retrieve accounts
     * @return A ResponseEntity containing a list of Account objects and an OK status code (200) upon success.
     * @throws UsernameNotFoundException If the specified username is not found
     * @throws Exception For any other unexpected errors
     */
    @GetMapping("/{username}/accounts") // Endpoint for getting user accounts
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
