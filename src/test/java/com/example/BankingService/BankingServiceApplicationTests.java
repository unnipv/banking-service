package com.example.BankingService;

import com.example.BankingService.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BankingServiceApplicationTests {
    @Autowired
    private UserService userService;
    @Test
    public void contextLoads() {
        // Application context should start without errors
    }

    @Test
    public void testBeanLoading() {
        assertNotNull(userService);
    }

}
