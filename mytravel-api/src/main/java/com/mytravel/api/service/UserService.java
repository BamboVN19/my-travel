package com.mytravel.api.service;

import com.mytravel.api.dto.RegisterRequest;
import com.mytravel.api.entity.User;
import com.mytravel.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // Logic Đăng ký
  public User register(RegisterRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new RuntimeException("Username đã tồn tại!");
    }
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new RuntimeException("Email đã tồn tại!");
    }

    User user = User.builder()
      .username(request.getUsername())
      .email(request.getEmail())
      .fullName(request.getFullName())
      .passwordHash(passwordEncoder.encode(request.getPassword())) // Mã hóa BCrypt
      .build();

    return userRepository.save(user);
  }

  // Logic Đăng nhập
  public User login(String username, String password) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new RuntimeException("Mật khẩu không chính xác!");
    }
    return user;
  }
}
