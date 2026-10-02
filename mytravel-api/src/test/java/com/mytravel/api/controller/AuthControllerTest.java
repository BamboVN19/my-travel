package com.mytravel.api.controller;

import com.mytravel.api.dto.RegisterRequest;
import com.mytravel.api.entity.User;
import com.mytravel.api.security.JwtUtils;
import com.mytravel.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        mockUser = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@example.com")
                .fullName("John Doe")
                .passwordHash("hashed_password")
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 200 and User JSON when registration is successful")
    void register_Success_Returns200() throws Exception {
        when(userService.register(any(RegisterRequest.class))).thenReturn(mockUser);

        String requestBody = """
                {
                    "username": "johndoe",
                    "password": "plainPassword123",
                    "email": "john@example.com",
                    "fullName": "John Doe"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 when registration fails")
    void register_Failure_Returns400() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username đã tồn tại!"));

        String requestBody = """
                {
                    "username": "johndoe",
                    "password": "plainPassword123",
                    "email": "john@example.com",
                    "fullName": "John Doe"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON, MediaType.valueOf("text/plain;charset=UTF-8"))
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("Username đã tồn tại!", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 200 and LoginResponse with token when credentials are valid")
    void login_Success_Returns200WithToken() throws Exception {
        when(userService.login("johndoe", "plainPassword123")).thenReturn(mockUser);
        when(jwtUtils.generateToken("johndoe")).thenReturn("mocked.jwt.token");

        String requestBody = """
                {
                    "username": "johndoe",
                    "password": "plainPassword123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("johndoe"));

        verify(userService, times(1)).login("johndoe", "plainPassword123");
        verify(jwtUtils, times(1)).generateToken("johndoe");
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 when password is wrong")
    void login_WrongPassword_Returns401() throws Exception {
        when(userService.login("johndoe", "wrongPassword"))
                .thenThrow(new RuntimeException("Mật khẩu không chính xác!"));

        String requestBody = """
                {
                    "username": "johndoe",
                    "password": "wrongPassword"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON, MediaType.valueOf("text/plain;charset=UTF-8"))
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals("Mật khẩu không chính xác!", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        verify(userService, times(1)).login("johndoe", "wrongPassword");
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 when account does not exist")
    void login_UserNotFound_Returns401() throws Exception {
        when(userService.login("unknown", "anyPassword"))
                .thenThrow(new RuntimeException("Tài khoản không tồn tại!"));

        String requestBody = """
                {
                    "username": "unknown",
                    "password": "anyPassword"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON, MediaType.valueOf("text/plain;charset=UTF-8"))
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals("Tài khoản không tồn tại!", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        verify(userService, times(1)).login("unknown", "anyPassword");
        verify(jwtUtils, never()).generateToken(anyString());
    }
}
