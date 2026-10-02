package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Column(name = "avatar_url", columnDefinition = "TEXT")
  private String avatarUrl;

  @Builder.Default
  @Column(length = 20)
  private String status = "ACTIVE";

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
    if (status == null) {
      status = "ACTIVE";
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
