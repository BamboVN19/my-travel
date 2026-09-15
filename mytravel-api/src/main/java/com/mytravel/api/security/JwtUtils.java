package com.mytravel.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
  // Key này phải dài ít nhất 32 ký tự
  private static final String JWT_SECRET = "5jBU68F3idBqSKY49g9Uy8lIps0rgMQtvTKo64ZTHHQ=";
  private static final long JWT_EXPIRATION = 604800000L;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

    return Jwts.builder()
      .subject(username)
      .issuedAt(now)
      .expiration(expiryDate)
      .signWith(getSigningKey())
      .compact();
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
    }
    return false;
  }
}
