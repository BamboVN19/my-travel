package com.mytravel.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_resets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordReset {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false, length = 100)
  private String email;

  @Column(name = "otp_code", nullable = false, length = 6)
  private String otpCode;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Builder.Default
  @Column(name = "is_used", nullable = false)
  private boolean isUsed = false;

  @Builder.Default
  @Column(name = "attempt_count", nullable = false)
  private int attemptCount = 0;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
