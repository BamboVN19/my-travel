package com.mytravel.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SecurityConfig Unit Tests")
class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    @DisplayName("passwordEncoder should return BCryptPasswordEncoder and encode/match correctly")
    void passwordEncoder_ReturnsBCryptPasswordEncoderAndWorksCorrectly() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);

        String rawPassword = "mySecretPassword123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        assertFalse(encoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    @DisplayName("filterChain should configure CSRF, authorization and build SecurityFilterChain")
    @SuppressWarnings("unchecked")
    void filterChain_ConfiguresHttpSecurityProperly() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        DefaultSecurityFilterChain mockChain = mock(DefaultSecurityFilterChain.class);

        when(http.csrf(any(Customizer.class))).thenReturn(http);
        when(http.authorizeHttpRequests(any(Customizer.class))).thenReturn(http);
        when(http.build()).thenReturn(mockChain);

        SecurityFilterChain chain = securityConfig.filterChain(http);

        assertNotNull(chain);
        assertSame(mockChain, chain);
        verify(http, times(1)).csrf(any(Customizer.class));
        verify(http, times(1)).authorizeHttpRequests(any(Customizer.class));
        verify(http, times(1)).build();
    }
}
