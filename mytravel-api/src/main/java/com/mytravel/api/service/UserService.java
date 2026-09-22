package com.mytravel.api.service;

import com.mytravel.api.dto.RegisterRequest;
import com.mytravel.api.dto.UserProfileResponse;
import com.mytravel.api.entity.User;
import com.mytravel.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // Logic Đăng ký - Mật khẩu được mã hóa BCrypt trước khi lưu vào DB
  public UserProfileResponse register(RegisterRequest request) {
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
      .passwordHash(passwordEncoder.encode(request.getPassword())) // Mã hóa 1 chiều BCrypt + Salt ngẫu nhiên
      .build();

    User savedUser = userRepository.save(user);

    return UserProfileResponse.builder()
      .id(savedUser.getId())
      .username(savedUser.getUsername())
      .email(savedUser.getEmail())
      .fullName(savedUser.getFullName())
      .phoneNumber(savedUser.getPhoneNumber())
      .avatarUrl(savedUser.getAvatarUrl())
      .createdAt(savedUser.getCreatedAt())
      .build();
  }

  // Logic Đăng nhập - So sánh mật khẩu plain-text với passwordHash trong DB qua BCrypt
  public User login(String username, String password) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new RuntimeException("Mật khẩu không chính xác!");
    }
    return user;
  }

  // Lấy người dùng hiện tại đang đăng nhập
  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
      throw new RuntimeException("Người dùng chưa được xác thực!");
    }
    String username = (String) auth.getPrincipal();
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
  }

  // Lấy thông tin cá nhân
  public UserProfileResponse getCurrentUserProfile() {
    User user = getCurrentUser();
    return UserProfileResponse.builder()
      .id(user.getId())
      .username(user.getUsername())
      .email(user.getEmail())
      .fullName(user.getFullName())
      .phoneNumber(user.getPhoneNumber())
      .avatarUrl(user.getAvatarUrl())
      .createdAt(user.getCreatedAt())
      .build();
  }
}
