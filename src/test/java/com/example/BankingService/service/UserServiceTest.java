package com.example.BankingService.service;

import com.example.BankingService.model.User;
import com.example.BankingService.repository.AccountRepository;
import com.example.BankingService.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class) // Replace with @SpringRunner if using older Spring Boot versions
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AccountRepository accountRepository;

    @Test
    public void testCreateUser_Success() {
        String username = "testuser";
        String password = "password";
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail("test");
        user.setPhoneNumber("test");

        // Mock userRepository behavior
        when(userRepository.existsByUsername(username)).thenReturn(false); // Username doesn't exist

        String result = userService.createUser(user);

        assertEquals("Registration successful", result);

        // Verify password is hashed and user is saved
        verify(userRepository).save(user);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertNotEquals(password, savedUser.getPassword()); // Password should be hashed
    }

    @Test
    public void testCreateUser_UsernameExists() {
        String username = "testuser";
        String password = "password";
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail("test");
        user.setPhoneNumber("test");

        // Mock userRepository behavior
        when(userRepository.existsByUsername(username)).thenReturn(true); // Username already exists

        String result = userService.createUser(user);

        assertEquals("Username already exists.", result);
        verify(userRepository, times(0)).save(user); // User not saved
    }

    @Test
    public void testCreateUser_RegistrationFailure() {
        String username = "testuser";
        String password = "password";
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail("test");
        user.setPhoneNumber("test");

        // Mock userRepository behavior to throw an exception
        when(userRepository.existsByUsername(username)).thenReturn(false);
        doThrow(new RuntimeException("Database error")).when(userRepository).save(user);

        String result = userService.createUser(user);

        assertEquals("Registration failed.", result);
        verify(userRepository).save(user); // User save attempted
    }

    @Test
    public void testCheckUsernameExists_Found() {
        String username = "testuser";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean exists = userService.checkUsernameExists(username);

        Assertions.assertTrue(exists);
    }

    @Test
    public void testCheckUsernameExists_NotFound() {
        String username = "testuser";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        boolean exists = userService.checkUsernameExists(username);

        assertFalse(exists);
    }
}

