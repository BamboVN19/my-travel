package com.mytravel.api.controller;

import com.mytravel.api.dto.LoginRequest;
import com.mytravel.api.dto.LoginResponse;
import com.mytravel.api.dto.RegisterRequest;
import com.mytravel.api.entity.User;
import com.mytravel.api.security.JwtUtils;
import com.mytravel.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtUtils jwtUtils;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    try {
      return ResponseEntity.ok(userService.register(request));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      User user = userService.login(request.getUsername(), request.getPassword());
      String token = jwtUtils.generateToken(user.getUsername());
      return ResponseEntity.ok(new LoginResponse(token, "Bearer", user.getUsername()));
    } catch (Exception e) {
      return ResponseEntity.status(401).body(e.getMessage());
    }
  }
}
