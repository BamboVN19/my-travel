package com.mytravel.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

  private static final String JWT_SECRET = "5jBU68F3idBqSKY49g9Uy8lIps0rgMQtvTKo64ZTHHQ=";
  private static final long ACCESS_TOKEN_EXPIRATION = 86400000L; // 24 hours in ms

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

    return Jwts.builder()
        .subject(username)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
  }

  public String generateRefreshTokenString() {
    return UUID.randomUUID().toString();
  }

  public long getAccessTokenExpirationInSeconds() {
    return ACCESS_TOKEN_EXPIRATION / 1000;
  }

  public String getUsernameFromJwt(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(authToken);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Token JWT không hợp lệ: {}", e.getMessage());
    }
    return false;
  }
}
