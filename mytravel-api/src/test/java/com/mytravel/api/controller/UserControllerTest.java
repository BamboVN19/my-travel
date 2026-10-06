package com.mytravel.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytravel.api.dto.UpdateProfileRequest;
import com.mytravel.api.dto.UserProfileResponse;
import com.mytravel.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserProfileResponse sampleProfile;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        sampleProfile = UserProfileResponse.builder()
                .id(UUID.randomUUID())
                .username("travel_fan")
                .fullName("Pham Thi B")
                .email("fan@mytravel.com")
                .avatarUrl("/uploads/avatars/avatar.jpg")
                .build();
    }

    @Test
    @DisplayName("GET /users/me - Success returns 200")
    void getCurrentUser_Success() throws Exception {
        when(userService.getCurrentUserProfile()).thenReturn(sampleProfile);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("travel_fan"))
                .andExpect(jsonPath("$.email").value("fan@mytravel.com"));

        verify(userService, times(1)).getCurrentUserProfile();
    }

    @Test
    @DisplayName("PUT /users/me - Success returns 200")
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .fullName("Pham Thi B Updated")
                .phoneNumber("0912345678")
                .build();

        when(userService.updateProfile(any(UpdateProfileRequest.class))).thenReturn(sampleProfile);

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("travel_fan"));

        verify(userService, times(1)).updateProfile(any(UpdateProfileRequest.class));
    }

    @Test
    @DisplayName("POST /users/me/avatar - Success returns 200")
    void updateAvatar_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "new_avatar.jpg", "image/jpeg", "avatar content".getBytes()
        );

        when(userService.updateAvatar(any())).thenReturn(sampleProfile);

        mockMvc.perform(multipart("/users/me/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("/uploads/avatars/avatar.jpg"));

        verify(userService, times(1)).updateAvatar(any());
    }
}
