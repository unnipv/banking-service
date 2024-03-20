package com.example.BankingService.controller;

import com.example.BankingService.dto.AuthRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    public void testAuthenticate_Success() {
        String username = "testuser";
        String password = "password";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // Mock successful authentication
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(null, null) // Token with null principal
        )).thenReturn(null);

        ResponseEntity<?> response = authenticationController.authenticate(new AuthRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAuthenticate_Failure() {
        String username = "testuser";
        String password = "wrongpassword";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // Mock authentication failure
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(null, null) // Token with null principal
        )).thenThrow(new BadCredentialsException("Invalid username or password"));

        ResponseEntity<?> response = authenticationController.authenticate(new AuthRequest());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }
}
