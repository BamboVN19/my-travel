package com.mytravel.api.service;

import com.mytravel.api.dto.*;
import com.mytravel.api.entity.PasswordReset;
import com.mytravel.api.entity.RefreshToken;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.DuplicateResourceException;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.exception.UnauthorizedException;
import com.mytravel.api.repository.PasswordResetRepository;
import com.mytravel.api.repository.RefreshTokenRepository;
import com.mytravel.api.repository.UserRepository;
import com.mytravel.api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordResetRepository passwordResetRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final UserService userService;
  private final EmailService emailService;
  private final TelegramService telegramService;

  @Transactional
  public UserProfileResponse register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new DuplicateResourceException("Username '" + request.getUsername() + "' đã tồn tại!");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateResourceException("Email '" + request.getEmail() + "' đã tồn tại!");
    }

    User user = User.builder()
        .username(request.getUsername().trim())
        .email(request.getEmail().trim().toLowerCase())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .fullName(request.getFullName() != null ? request.getFullName().trim() : null)
        .phoneNumber(request.getPhone() != null ? request.getPhone().trim() : null)
        .status("ACTIVE")
        .build();

    User savedUser = userRepository.save(user);
    return userService.mapToProfileResponse(savedUser);
  }

  @Transactional
  public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
        .orElseThrow(() -> new UnauthorizedException("Tài khoản hoặc mật khẩu không chính xác"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new UnauthorizedException("Tài khoản hoặc mật khẩu không chính xác");
    }

    String accessToken = jwtUtils.generateAccessToken(user.getUsername());
    String refreshTokenStr = jwtUtils.generateRefreshTokenString();

    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .token(refreshTokenStr)
        .expiryDate(LocalDateTime.now().plusDays(7))
        .revoked(false)
        .build();

    refreshTokenRepository.save(refreshToken);

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshTokenStr)
        .tokenType("Bearer")
        .expiresIn(jwtUtils.getAccessTokenExpirationInSeconds())
        .user(userService.mapToProfileResponse(user))
        .build();
  }

  @Transactional
  public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
    RefreshToken tokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> new UnauthorizedException("Refresh token không tồn tại"));

    if (tokenEntity.isRevoked()) {
      throw new UnauthorizedException("Refresh token đã bị thu hồi");
    }

    if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new UnauthorizedException("Refresh token đã hết hạn. Vui lòng đăng nhập lại!");
    }

    // Refresh Token Rotation: Revoke current refresh token & issue new pair
    tokenEntity.setRevoked(true);
    refreshTokenRepository.save(tokenEntity);

    User user = tokenEntity.getUser();
    String newAccessToken = jwtUtils.generateAccessToken(user.getUsername());
    String newRefreshTokenStr = jwtUtils.generateRefreshTokenString();

    RefreshToken newRefreshToken = RefreshToken.builder()
        .user(user)
        .token(newRefreshTokenStr)
        .expiryDate(LocalDateTime.now().plusDays(7))
        .revoked(false)
        .build();

    refreshTokenRepository.save(newRefreshToken);

    return RefreshTokenResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshTokenStr)
        .tokenType("Bearer")
        .expiresIn(jwtUtils.getAccessTokenExpirationInSeconds())
        .build();
  }

  @Transactional
  public void logout(LogoutRequest request) {
    refreshTokenRepository.findByToken(request.getRefreshToken())
        .ifPresent(token -> {
          token.setRevoked(true);
          refreshTokenRepository.save(token);
        });
  }

  @Transactional
  public void forgotPassword(ForgotPasswordRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email này"));

    SecureRandom random = new SecureRandom();
    String otp = String.format("%06d", random.nextInt(1000000));

    PasswordReset passwordReset = PasswordReset.builder()
        .email(user.getEmail())
        .otpCode(otp)
        .expiresAt(LocalDateTime.now().plusMinutes(10))
        .isUsed(false)
        .attemptCount(0)
        .build();

    passwordResetRepository.save(passwordReset);

    log.info("[OTP FORGOT PASSWORD] Email: {}, OTP Code: {}", user.getEmail(), otp);
    emailService.sendOtpEmail(user.getEmail(), otp);
    telegramService.sendOtpNotification(user.getEmail(), otp);
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    PasswordReset reset = passwordResetRepository.findByEmailAndOtpCodeAndIsUsedFalse(request.getEmail(), request.getOtpCode())
        .orElseThrow(() -> new BadRequestException("Mã OTP không chính xác hoặc đã được sử dụng"));

    if (reset.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BadRequestException("Mã OTP đã hết hạn");
    }

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    reset.setUsed(true);
    passwordResetRepository.save(reset);
  }

  @Transactional
  public void changePassword(ChangePasswordRequest request) {
    User user = userService.getCurrentUser();

    if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
      throw new BadRequestException("Mật khẩu hiện tại không chính xác");
    }

    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }
}
