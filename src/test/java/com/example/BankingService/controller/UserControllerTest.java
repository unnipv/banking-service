package com.example.BankingService.controller;

import com.example.BankingService.controller.UserController;
import com.example.BankingService.model.Account;
import com.example.BankingService.model.User;
import com.example.BankingService.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService mockUserService; // Assuming a mocked UserService

    @MockBean // Optional if using a real UserService bean
    private UserService userService; // Mock the UserService for controlled behavior

    @Test
    public void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setPassword("test");
        user.setEmail("test");
        user.setName("test");
        user.setPhoneNumber("test");
        String expectedResponse = "Registration successful";
        Mockito.when(mockUserService.createUser(user)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = userController.registerUser(user);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // Optionally, verify the response body if it contains additional information
    }

    @Test
    public void testRegisterUser_Failure() throws Exception {
        // Arrange (Set up mock behavior)
        User user = new User();
        user.setEmail("test");
        user.setName("test");
        user.setPhoneNumber("test");
        Mockito.when(mockUserService.createUser(user)).thenThrow(new RuntimeException("Failed to create user"));

        // Act
        ResponseEntity<?> response = userController.registerUser(user);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to create user"));
    }

    @Test
    public void testGetUserAccounts_Success() throws Exception {
        // Arrange (Set up mock behavior)
        String username = "testuser";
        List<Account> mockAccounts = List.of(new Account());
        Mockito.when(mockUserService.getUserAccounts(username)).thenReturn(mockAccounts);

        // Act
        ResponseEntity<?> response = userController.getUserAccounts(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAccounts, response.getBody());
    }

    @Test
    public void testGetUserAccounts_UserNotFound() throws Exception {
        // Arrange (Set up mock behavior)
        String username = "notfound";
        Mockito.when(mockUserService.getUserAccounts(username)).thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        ResponseEntity<?> response = userController.getUserAccounts(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));
    }

    @Test
    public void testGetUserAccounts_InternalServerError() throws Exception {
        // Arrange (Set up mock behavior)
        String username = "testuser";
        Mockito.when(mockUserService.getUserAccounts(username)).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = userController.getUserAccounts(username);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to retrieve user accounts"));
    }
}
