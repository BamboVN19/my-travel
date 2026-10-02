package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
  private UUID id;
  private String username;
  private String email;
  private String fullName;
  private String phoneNumber;
  private String avatarUrl;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime createdAt;
}
