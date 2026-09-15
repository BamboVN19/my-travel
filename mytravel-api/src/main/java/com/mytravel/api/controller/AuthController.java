package com.mytravel.api.controller;

import com.mytravel.api.dto.LoginRequest;
import com.mytravel.api.dto.LoginResponse;
import com.mytravel.api.dto.RegisterRequest;
import com.mytravel.api.entity.User;
import com.mytravel.api.security.JwtUtils;
import com.mytravel.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    try {
      return ResponseEntity.ok(userService.register(request));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Autowired
  private JwtUtils jwtUtils; // Tiêm công cụ JWT vào

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      // 1. Kiểm tra username/password (như cũ)
      User user = userService.login(request.getUsername(), request.getPassword());

      // 2. Nếu OK, tạo Access Token
      String token = jwtUtils.generateToken(user.getUsername());

      // 3. Trả về cho Client
      return ResponseEntity.ok(new LoginResponse(token, "Bearer", user.getUsername()));
    } catch (Exception e) {
      return ResponseEntity.status(401).body(e.getMessage());
    }
  }
}
