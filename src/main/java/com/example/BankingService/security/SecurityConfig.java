package com.example.BankingService.security;

import com.example.BankingService.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This configuration class enables and configures Spring Security for the application.
 * It defines password encoding, authentication management, and access control rules.
 *
 * @author Unni
 */
@Configuration
@EnableWebSecurity // Enables Spring Security features
public class SecurityConfig {

    /**
     * Injected UserDetailsServiceImpl dependency to retrieve user information for authentication.
     */
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    /**
     * Creates a BCryptPasswordEncoder bean for secure password encoding.
     *
     * @return The BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an AuthenticationManager bean using the provided AuthenticationConfiguration.
     *
     * @param authenticationConfiguration The AuthenticationConfiguration object
     * @return The AuthenticationManager instance
     * @throws Exception If there's an error during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the SecurityFilterChain for authentication and authorization rules.
     *
     * @param httpSecurity The HttpSecurity object for configuring security
     * @return The configured SecurityFilterChain
     * @throws Exception If there's an error during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Configure authentication manager
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Configure security rules
        httpSecurity
                .csrf().disable() // Disable CSRF protection for simplicity in this example
                .authorizeHttpRequests()
                .requestMatchers("/api/users/register", "/api/authenticate").permitAll() // Allow access to registration and login endpoints without authentication
                .anyRequest() // Require authentication for all other requests
                .authenticated()
                .and()
                .authenticationManager(authenticationManager) // Set the authentication manager
                .httpBasic(); // Use HTTP Basic authentication
        return httpSecurity.build();
    }
}

