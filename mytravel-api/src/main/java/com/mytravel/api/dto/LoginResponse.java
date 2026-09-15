package com.mytravel.api.dto;
import lombok.*;

@Data
@AllArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String tokenType = "Bearer";
  private String username;
}
