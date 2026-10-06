package com.mytravel.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtils Unit Tests")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    @DisplayName("generateAccessToken and getUsernameFromJwt - Should generate valid token and extract username")
    void generateAccessToken_And_GetUsername_Success() {
        String username = "traveler_nguyen";
        String token = jwtUtils.generateAccessToken(username);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String extractedUsername = jwtUtils.getUsernameFromJwt(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("validateToken - Should return true for valid token")
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtils.generateAccessToken("valid_user");
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("validateToken - Should return false for malformed or null token")
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken("invalid.jwt.token"));
        assertFalse(jwtUtils.validateToken(""));
        assertFalse(jwtUtils.validateToken("abcxyz"));
    }

    @Test
    @DisplayName("generateRefreshTokenString - Should return valid UUID string")
    void generateRefreshTokenString_ReturnsUuidString() {
        String refreshToken = jwtUtils.generateRefreshTokenString();
        assertNotNull(refreshToken);
        assertEquals(36, refreshToken.length());
        assertTrue(refreshToken.contains("-"));
    }

    @Test
    @DisplayName("getAccessTokenExpirationInSeconds - Should return 86400 (24 hours)")
    void getAccessTokenExpirationInSeconds_ReturnsCorrectValue() {
        assertEquals(86400L, jwtUtils.getAccessTokenExpirationInSeconds());
    }
}
