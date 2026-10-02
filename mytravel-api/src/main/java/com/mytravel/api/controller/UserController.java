package com.mytravel.api.controller;

import com.mytravel.api.dto.UpdateProfileRequest;
import com.mytravel.api.dto.UserProfileResponse;
import com.mytravel.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getCurrentUser() {
    UserProfileResponse profile = userService.getCurrentUserProfile();
    return ResponseEntity.ok(profile);
  }

  @PutMapping("/me")
  public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
    UserProfileResponse updated = userService.updateProfile(request);
    return ResponseEntity.ok(updated);
  }

  @PostMapping("/me/avatar")
  public ResponseEntity<UserProfileResponse> updateAvatar(@RequestParam("file") MultipartFile file) {
    UserProfileResponse updated = userService.updateAvatar(file);
    return ResponseEntity.ok(updated);
  }
}
