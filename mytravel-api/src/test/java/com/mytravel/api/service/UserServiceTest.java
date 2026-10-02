package com.mytravel.api.service;

import com.mytravel.api.dto.RegisterRequest;
import com.mytravel.api.entity.User;
import com.mytravel.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("johndoe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("plainPassword123");
        registerRequest.setFullName("John Doe");

        mockUser = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@example.com")
                .fullName("John Doe")
                .passwordHash("hashed_password_xyz")
                .build();
    }

    @Test
    @DisplayName("register should succeed when username and email are unique")
    void register_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword123")).thenReturn("hashed_password_xyz");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("hashed_password_xyz", result.getPasswordHash());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("johndoe", savedUser.getUsername());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("hashed_password_xyz", savedUser.getPasswordHash());
        assertEquals("John Doe", savedUser.getFullName());
    }

    @Test
    @DisplayName("register should throw RuntimeException when username already exists")
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(registerRequest));

        assertEquals("Username đã tồn tại!", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register should throw RuntimeException when email already exists")
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(registerRequest));

        assertEquals("Email đã tồn tại!", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login should succeed when credentials are correct")
    void login_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("plainPassword123", "hashed_password_xyz")).thenReturn(true);

        User result = userService.login("johndoe", "plainPassword123");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals(1L, result.getId());
        verify(userRepository).findByUsername("johndoe");
        verify(passwordEncoder).matches("plainPassword123", "hashed_password_xyz");
    }

    @Test
    @DisplayName("login should throw RuntimeException when user does not exist")
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login("nonexistent", "secretPass"));

        assertEquals("Tài khoản không tồn tại!", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("login should throw RuntimeException when password does not match")
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPass", "hashed_password_xyz")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login("johndoe", "wrongPass"));

        assertEquals("Mật khẩu không chính xác!", exception.getMessage());
        verify(passwordEncoder).matches("wrongPass", "hashed_password_xyz");
    }
}
