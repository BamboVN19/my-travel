package com.mytravel.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytravel.api.dto.*;
import com.mytravel.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
    }

    @Test
    @DisplayName("POST /auth/register - Success returns 201")
    void register_Success() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("newtraveler")
                .email("new@mytravel.com")
                .password("password123")
                .fullName("Nguyen Van New")
                .build();

        UserProfileResponse response = UserProfileResponse.builder()
                .id(UUID.randomUUID())
                .username("newtraveler")
                .email("new@mytravel.com")
                .fullName("Nguyen Van New")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newtraveler"))
                .andExpect(jsonPath("$.email").value("new@mytravel.com"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login - Success returns 200")
    void login_Success() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("newtraveler")
                .password("password123")
                .build();

        LoginResponse response = LoginResponse.builder()
                .accessToken("mocked.access.token")
                .refreshToken("mocked.refresh.token")
                .tokenType("Bearer")
                .expiresIn(86400L)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked.access.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/refresh-token - Success returns 200")
    void refreshToken_Success() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("old.refresh.token")
                .build();

        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken("new.access.token")
                .refreshToken("new.refresh.token")
                .tokenType("Bearer")
                .expiresIn(86400L)
                .build();

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new.access.token"));

        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("POST /auth/logout - Success returns 200")
    void logout_Success() throws Exception {
        LogoutRequest request = LogoutRequest.builder()
                .refreshToken("logout.token")
                .build();

        doNothing().when(authService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đăng xuất thành công"));

        verify(authService, times(1)).logout(any(LogoutRequest.class));
    }

    @Test
    @DisplayName("POST /auth/forgot-password - Success returns 200")
    void forgotPassword_Success() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("user@mytravel.com")
                .build();

        doNothing().when(authService).forgotPassword(any(ForgotPasswordRequest.class));

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mã OTP đã được gửi đến email của bạn"));

        verify(authService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    @DisplayName("POST /auth/reset-password - Success returns 200")
    void resetPassword_Success() throws Exception {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("user@mytravel.com")
                .otpCode("123456")
                .newPassword("brandNewPassword123")
                .build();

        doNothing().when(authService).resetPassword(any(ResetPasswordRequest.class));

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đặt lại mật khẩu thành công!"));

        verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    @DisplayName("POST /auth/change-password - Success returns 200")
    void changePassword_Success() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPassword123")
                .newPassword("brandNewPassword123")
                .build();

        doNothing().when(authService).changePassword(any(ChangePasswordRequest.class));

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đổi mật khẩu thành công!"));

        verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
    }
}
