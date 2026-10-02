package com.mytravel.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtils Unit Tests")
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private static final String TEST_SECRET = "5jBU68F3idBqSKY49g9Uy8lIps0rgMQtvTKo64ZTHHQ=";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    @DisplayName("generateToken should return a non-null, non-empty JWT string")
    void generateToken_ValidUsername_ReturnsNonNullToken() {
        String token = jwtUtils.generateToken("testuser");

        assertNotNull(token);
        assertFalse(token.trim().isEmpty());
        // JWT format consists of 3 parts separated by dots
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("getUsernameFromJwt should extract the correct username")
    void getUsernameFromJwt_ValidToken_ReturnsUsername() {
        String expectedUsername = "traveler_123";
        String token = jwtUtils.generateToken(expectedUsername);

        String actualUsername = jwtUtils.getUsernameFromJwt(token);

        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    @DisplayName("validateToken should return true for a valid token")
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtils.generateToken("validuser");

        boolean isValid = jwtUtils.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("validateToken should return false for malformed token")
    void validateToken_MalformedToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken("not.a.valid.jwt.token"));
        assertFalse(jwtUtils.validateToken("some-random-string"));
    }

    @Test
    @DisplayName("validateToken should return false for tampered token")
    void validateToken_TamperedToken_ReturnsFalse() {
        String token = jwtUtils.generateToken("originaluser");
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";

        assertFalse(jwtUtils.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("validateToken should return false for null or empty token")
    void validateToken_NullOrEmptyToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken(null));
        assertFalse(jwtUtils.validateToken(""));
        assertFalse(jwtUtils.validateToken("   "));
    }

    @Test
    @DisplayName("validateToken should return false for expired token")
    void validateToken_ExpiredToken_ReturnsFalse() {
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        Date past = new Date(System.currentTimeMillis() - 100000);
        Date expiredAt = new Date(System.currentTimeMillis() - 50000);

        String expiredToken = Jwts.builder()
                .subject("expired_user")
                .issuedAt(past)
                .expiration(expiredAt)
                .signWith(key)
                .compact();

        assertFalse(jwtUtils.validateToken(expiredToken));
    }
}
