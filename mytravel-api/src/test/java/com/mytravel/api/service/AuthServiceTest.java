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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private TelegramService telegramService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .username("testtraveler")
                .email("test@mytravel.com")
                .passwordHash("hashed_password_123")
                .fullName("Nguyen Van A")
                .phoneNumber("0912345678")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("register - Success")
    void register_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testtraveler")
                .email("test@mytravel.com")
                .password("plain123")
                .fullName("Nguyen Van A")
                .phone("0912345678")
                .build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserProfileResponse mockProfile = UserProfileResponse.builder()
                .id(sampleUser.getId())
                .username(sampleUser.getUsername())
                .email(sampleUser.getEmail())
                .fullName(sampleUser.getFullName())
                .build();
        when(userService.mapToProfileResponse(any(User.class))).thenReturn(mockProfile);

        UserProfileResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("testtraveler", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register - Duplicate Username throws DuplicateResourceException")
    void register_DuplicateUsername_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testtraveler")
                .email("test@mytravel.com")
                .password("plain123")
                .build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register - Duplicate Email throws DuplicateResourceException")
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("test@mytravel.com")
                .password("plain123")
                .build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login - Success")
    void login_Success() {
        LoginRequest request = LoginRequest.builder()
                .username("testtraveler")
                .password("correct_password")
                .build();

        when(userRepository.findByUsernameOrEmail("testtraveler", "testtraveler")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("correct_password", sampleUser.getPasswordHash())).thenReturn(true);
        when(jwtUtils.generateAccessToken(sampleUser.getUsername())).thenReturn("access_token_xyz");
        when(jwtUtils.generateRefreshTokenString()).thenReturn("refresh_token_uuid");
        when(jwtUtils.getAccessTokenExpirationInSeconds()).thenReturn(86400L);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access_token_xyz", response.getAccessToken());
        assertEquals("refresh_token_uuid", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("login - User not found throws UnauthorizedException")
    void login_UserNotFound_ThrowsException() {
        LoginRequest request = LoginRequest.builder()
                .username("unknown_user")
                .password("password")
                .build();

        when(userRepository.findByUsernameOrEmail("unknown_user", "unknown_user")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("login - Wrong password throws UnauthorizedException")
    void login_WrongPassword_ThrowsException() {
        LoginRequest request = LoginRequest.builder()
                .username("testtraveler")
                .password("wrong_password")
                .build();

        when(userRepository.findByUsernameOrEmail("testtraveler", "testtraveler")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong_password", sampleUser.getPasswordHash())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("refreshToken - Success with Rotation")
    void refreshToken_Success() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid_refresh_token")
                .build();

        RefreshToken oldToken = RefreshToken.builder()
                .token("valid_refresh_token")
                .user(sampleUser)
                .expiryDate(LocalDateTime.now().plusDays(2))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("valid_refresh_token")).thenReturn(Optional.of(oldToken));
        when(jwtUtils.generateAccessToken(sampleUser.getUsername())).thenReturn("new_access_token");
        when(jwtUtils.generateRefreshTokenString()).thenReturn("new_refresh_token");
        when(jwtUtils.getAccessTokenExpirationInSeconds()).thenReturn(86400L);

        RefreshTokenResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());
        assertTrue(oldToken.isRevoked());
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("refreshToken - Token not found throws UnauthorizedException")
    void refreshToken_NotFound_ThrowsException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("missing_token")
                .build();

        when(refreshTokenRepository.findByToken("missing_token")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.refreshToken(request));
    }

    @Test
    @DisplayName("refreshToken - Revoked token throws UnauthorizedException")
    void refreshToken_Revoked_ThrowsException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("revoked_token")
                .build();

        RefreshToken token = RefreshToken.builder()
                .token("revoked_token")
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByToken("revoked_token")).thenReturn(Optional.of(token));

        assertThrows(UnauthorizedException.class, () -> authService.refreshToken(request));
    }

    @Test
    @DisplayName("refreshToken - Expired token throws UnauthorizedException")
    void refreshToken_Expired_ThrowsException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("expired_token")
                .build();

        RefreshToken token = RefreshToken.builder()
                .token("expired_token")
                .revoked(false)
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();

        when(refreshTokenRepository.findByToken("expired_token")).thenReturn(Optional.of(token));

        assertThrows(UnauthorizedException.class, () -> authService.refreshToken(request));
    }

    @Test
    @DisplayName("logout - Success marks token revoked")
    void logout_Success() {
        LogoutRequest request = LogoutRequest.builder()
                .refreshToken("logout_token")
                .build();

        RefreshToken token = RefreshToken.builder()
                .token("logout_token")
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("logout_token")).thenReturn(Optional.of(token));

        authService.logout(request);

        assertTrue(token.isRevoked());
        verify(refreshTokenRepository, times(1)).save(token);
    }

    @Test
    @DisplayName("forgotPassword - Success creates OTP and triggers Email & Telegram")
    void forgotPassword_Success() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("test@mytravel.com")
                .build();

        when(userRepository.findByEmail("test@mytravel.com")).thenReturn(Optional.of(sampleUser));

        authService.forgotPassword(request);

        verify(passwordResetRepository, times(1)).save(any(PasswordReset.class));
        verify(emailService, times(1)).sendOtpEmail(eq(sampleUser.getEmail()), anyString());
        verify(telegramService, times(1)).sendOtpNotification(eq(sampleUser.getEmail()), anyString());
    }

    @Test
    @DisplayName("forgotPassword - Email not found throws ResourceNotFoundException")
    void forgotPassword_EmailNotFound_ThrowsException() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("nonexistent@mytravel.com")
                .build();

        when(userRepository.findByEmail("nonexistent@mytravel.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.forgotPassword(request));
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("resetPassword - Success updates user password and marks OTP used")
    void resetPassword_Success() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("test@mytravel.com")
                .otpCode("123456")
                .newPassword("brandNewPassword123")
                .build();

        PasswordReset reset = PasswordReset.builder()
                .email("test@mytravel.com")
                .otpCode("123456")
                .isUsed(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        when(passwordResetRepository.findByEmailAndOtpCodeAndIsUsedFalse("test@mytravel.com", "123456"))
                .thenReturn(Optional.of(reset));
        when(userRepository.findByEmail("test@mytravel.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("brandNewPassword123")).thenReturn("encoded_new_pass");

        authService.resetPassword(request);

        assertTrue(reset.isUsed());
        assertEquals("encoded_new_pass", sampleUser.getPasswordHash());
        verify(userRepository, times(1)).save(sampleUser);
        verify(passwordResetRepository, times(1)).save(reset);
    }

    @Test
    @DisplayName("resetPassword - Invalid OTP throws BadRequestException")
    void resetPassword_InvalidOtp_ThrowsException() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("test@mytravel.com")
                .otpCode("999999")
                .newPassword("brandNewPassword123")
                .build();

        when(passwordResetRepository.findByEmailAndOtpCodeAndIsUsedFalse("test@mytravel.com", "999999"))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.resetPassword(request));
    }

    @Test
    @DisplayName("resetPassword - Expired OTP throws BadRequestException")
    void resetPassword_ExpiredOtp_ThrowsException() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("test@mytravel.com")
                .otpCode("123456")
                .newPassword("brandNewPassword123")
                .build();

        PasswordReset reset = PasswordReset.builder()
                .email("test@mytravel.com")
                .otpCode("123456")
                .isUsed(false)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(passwordResetRepository.findByEmailAndOtpCodeAndIsUsedFalse("test@mytravel.com", "123456"))
                .thenReturn(Optional.of(reset));

        assertThrows(BadRequestException.class, () -> authService.resetPassword(request));
    }

    @Test
    @DisplayName("changePassword - Success")
    void changePassword_Success() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("old_pass")
                .newPassword("new_pass")
                .build();

        when(userService.getCurrentUser()).thenReturn(sampleUser);
        when(passwordEncoder.matches("old_pass", sampleUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode("new_pass")).thenReturn("encoded_new_pass");

        authService.changePassword(request);

        assertEquals("encoded_new_pass", sampleUser.getPasswordHash());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("changePassword - Wrong old password throws BadRequestException")
    void changePassword_WrongOldPassword_ThrowsException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("wrong_old_pass")
                .newPassword("new_pass")
                .build();

        when(userService.getCurrentUser()).thenReturn(sampleUser);
        when(passwordEncoder.matches("wrong_old_pass", sampleUser.getPasswordHash())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.changePassword(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
