package com.mytravel.api.service;

import com.mytravel.api.dto.UpdateProfileRequest;
import com.mytravel.api.dto.UserProfileResponse;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.exception.UnauthorizedException;
import com.mytravel.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .fullName("Nguyen Van Test")
                .phoneNumber("0987654321")
                .avatarUrl("/uploads/avatars/old.jpg")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(String username) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("getCurrentUser - Success when authenticated")
    void getCurrentUser_Success() {
        authenticateUser("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        User user = userService.getCurrentUser();

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    @DisplayName("getCurrentUser - Throws UnauthorizedException when unauthenticated or anonymous")
    void getCurrentUser_Unauthenticated_ThrowsException() {
        // No authentication in context
        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());

        // Anonymous user in context
        UsernamePasswordAuthenticationToken anonymous =
                new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(anonymous);
        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());
    }

    @Test
    @DisplayName("getCurrentUser - Throws ResourceNotFoundException when user not found in DB")
    void getCurrentUser_NotFoundInDb_ThrowsException() {
        authenticateUser("missing_user");
        when(userRepository.findByUsername("missing_user")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
    }

    @Test
    @DisplayName("getCurrentUserProfile - Success")
    void getCurrentUserProfile_Success() {
        authenticateUser("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        UserProfileResponse profile = userService.getCurrentUserProfile();

        assertNotNull(profile);
        assertEquals(sampleUser.getUsername(), profile.getUsername());
        assertEquals(sampleUser.getEmail(), profile.getEmail());
        assertEquals(sampleUser.getFullName(), profile.getFullName());
    }

    @Test
    @DisplayName("updateProfile - Success")
    void updateProfile_Success() {
        authenticateUser("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .fullName("Nguyen Van Updated")
                .phoneNumber("0911222333")
                .build();

        UserProfileResponse response = userService.updateProfile(request);

        assertNotNull(response);
        assertEquals("Nguyen Van Updated", response.getFullName());
        assertEquals("0911222333", response.getPhoneNumber());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("updateAvatar - Empty file throws BadRequestException")
    void updateAvatar_EmptyFile_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.updateAvatar(null));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);
        assertThrows(BadRequestException.class, () -> userService.updateAvatar(emptyFile));
    }

    @Test
    @DisplayName("updateAvatar - Success uploads and updates avatar URL")
    void updateAvatar_Success() {
        authenticateUser("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file", "new_avatar.png", "image/png", "fake avatar content".getBytes()
        );

        UserProfileResponse response = userService.updateAvatar(file);

        assertNotNull(response);
        assertTrue(response.getAvatarUrl().startsWith("/uploads/avatars/avatar_user_"));
        assertTrue(response.getAvatarUrl().endsWith(".png"));
    }
}
