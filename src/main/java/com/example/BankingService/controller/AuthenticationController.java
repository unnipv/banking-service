package com.example.BankingService.controller;

import com.example.BankingService.dto.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles user authentication, providing an endpoint for user login.
 *
 * @author Unni
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200") // Enables CORS for requests from the specified origin
@RequestMapping("/api") // Base path for authentication endpoint
public class AuthenticationController {

    /**
     * Injected AuthenticationManager dependency to perform user authentication.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Authenticates a user based on the provided credentials in the request body.
     *
     * @param request An AuthRequest object containing username and password for authentication.
     * @return A ResponseEntity with appropriate status code:
     *          - OK (200) on successful authentication
     *          - UNAUTHORIZED (401) for invalid credentials
     */
    @PostMapping("/authenticate") // Endpoint for user login
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            authenticationManager.authenticate(authenticationToken);
            return ResponseEntity.ok().build(); // Return a simple 200 OK response on successful authentication
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}

