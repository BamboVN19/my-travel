package com.mytravel.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytravel.api.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig Unit Tests")
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthenticationFilter);
    }

    @Test
    @DisplayName("passwordEncoder - Should encode and match password with BCrypt")
    void passwordEncoder_EncodesAndMatchesCorrectly() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);

        String rawPassword = "mySecretPassword123";
        String encoded = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encoded);
        assertTrue(encoder.matches(rawPassword, encoded));
        assertFalse(encoder.matches("wrongPassword", encoded));
    }

    @Test
    @DisplayName("objectMapper - Should register JavaTime module properly")
    void objectMapper_ConfiguredCorrectly() throws Exception {
        ObjectMapper mapper = securityConfig.objectMapper();
        assertNotNull(mapper);

        LocalDate date = LocalDate.of(2026, 10, 6);
        String json = mapper.writeValueAsString(date);
        assertNotNull(json);
    }

    @Test
    @DisplayName("filterChain - Configures HttpSecurity and SecurityFilterChain")
    void filterChain_BuildsSecurityFilterChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        DefaultSecurityFilterChain chain = mock(DefaultSecurityFilterChain.class);

        when(http.build()).thenReturn(chain);

        SecurityFilterChain result = securityConfig.filterChain(http, securityConfig.objectMapper());

        assertNotNull(result);
        verify(http, times(1)).addFilterBefore(eq(jwtAuthenticationFilter), eq(UsernamePasswordAuthenticationFilter.class));
        verify(http, times(1)).build();
    }
}
