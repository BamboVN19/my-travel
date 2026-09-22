package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "avatar_url")
  private String avatarUrl;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "refresh_token")
  private String refreshToken;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
