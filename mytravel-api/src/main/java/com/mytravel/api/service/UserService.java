package com.mytravel.api.service;

import com.mytravel.api.dto.UpdateProfileRequest;
import com.mytravel.api.dto.UserProfileResponse;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.exception.UnauthorizedException;
import com.mytravel.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
      throw new UnauthorizedException("Người dùng chưa được xác thực");
    }
    String username = (String) auth.getPrincipal();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng: " + username));
  }

  public UserProfileResponse getCurrentUserProfile() {
    User user = getCurrentUser();
    return mapToProfileResponse(user);
  }

  @Transactional
  public UserProfileResponse updateProfile(UpdateProfileRequest request) {
    User user = getCurrentUser();
    if (request.getFullName() != null) {
      user.setFullName(request.getFullName().trim());
    }
    if (request.getPhoneNumber() != null) {
      user.setPhoneNumber(request.getPhoneNumber().trim());
    }
    User updatedUser = userRepository.save(user);
    return mapToProfileResponse(updatedUser);
  }

  @Transactional
  public UserProfileResponse updateAvatar(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("File ảnh không được để trống");
    }
    User user = getCurrentUser();
    try {
      Path uploadDir = Paths.get("uploads", "avatars").toAbsolutePath().normalize();
      Files.createDirectories(uploadDir);

      String originalFilename = file.getOriginalFilename();
      String extension = "";
      if (originalFilename != null && originalFilename.contains(".")) {
        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
      }
      String fileName = "avatar_user_" + user.getId() + "_" + UUID.randomUUID() + extension;
      Path filePath = uploadDir.resolve(fileName);

      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      String avatarUrl = "/uploads/avatars/" + fileName;
      user.setAvatarUrl(avatarUrl);
      User updatedUser = userRepository.save(user);
      return mapToProfileResponse(updatedUser);
    } catch (IOException e) {
      log.error("Lỗi khi lưu file avatar: ", e);
      throw new BadRequestException("Không thể tải lên file avatar: " + e.getMessage());
    }
  }

  public UserProfileResponse mapToProfileResponse(User user) {
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
