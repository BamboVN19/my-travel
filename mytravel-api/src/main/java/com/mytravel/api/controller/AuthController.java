package com.mytravel.api.controller;

import com.mytravel.api.dto.*;
import com.mytravel.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<UserProfileResponse> register(@Valid @RequestBody RegisterRequest request) {
    UserProfileResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    RefreshTokenResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody LogoutRequest request) {
    authService.logout(request);
    return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    authService.forgotPassword(request);
    return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi đến email của bạn"));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
    return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công!"));
  }

  @PostMapping("/change-password")
  public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(request);
    return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
  }
}
