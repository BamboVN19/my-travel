package com.mytravel.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytravel.api.dto.AlbumPhotoResponse;
import com.mytravel.api.dto.AlbumRequest;
import com.mytravel.api.dto.AlbumResponse;
import com.mytravel.api.service.MediaService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MediaController Unit Tests")
class MediaControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    private UUID sampleTripId;
    private UUID sampleAlbumId;
    private UUID samplePhotoId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        sampleTripId = UUID.randomUUID();
        sampleAlbumId = UUID.randomUUID();
        samplePhotoId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /trips/{tripId}/albums - Success returns 200")
    void getAlbumsByTripId_Success() throws Exception {
        AlbumResponse album = AlbumResponse.builder()
                .id(sampleAlbumId)
                .tripId(sampleTripId)
                .albumTitle("Ảnh check-in")
                .build();

        when(mediaService.getAlbumsByTripId(sampleTripId)).thenReturn(List.of(album));

        mockMvc.perform(get("/trips/{tripId}/albums", sampleTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].albumTitle").value("Ảnh check-in"));

        verify(mediaService, times(1)).getAlbumsByTripId(sampleTripId);
    }

    @Test
    @DisplayName("POST /trips/{tripId}/albums - Success returns 201")
    void createAlbum_Success() throws Exception {
        AlbumRequest request = AlbumRequest.builder()
                .albumTitle("Ảnh check-in")
                .description("Mô tả album")
                .build();

        AlbumResponse album = AlbumResponse.builder()
                .id(sampleAlbumId)
                .tripId(sampleTripId)
                .albumTitle("Ảnh check-in")
                .build();

        when(mediaService.createAlbum(eq(sampleTripId), any(AlbumRequest.class))).thenReturn(album);

        mockMvc.perform(post("/trips/{tripId}/albums", sampleTripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.albumTitle").value("Ảnh check-in"));

        verify(mediaService, times(1)).createAlbum(eq(sampleTripId), any(AlbumRequest.class));
    }

    @Test
    @DisplayName("POST /albums/{albumId}/photos - Success returns 201")
    void uploadPhotos_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", "test.jpg", "image/jpeg", "image content".getBytes()
        );

        AlbumPhotoResponse photo = AlbumPhotoResponse.builder()
                .id(samplePhotoId)
                .albumId(sampleAlbumId)
                .caption("Cảnh đẹp")
                .photoUrl("/uploads/photos/test.jpg")
                .build();

        when(mediaService.uploadPhotos(eq(sampleAlbumId), any(), eq("Cảnh đẹp"))).thenReturn(List.of(photo));

        mockMvc.perform(multipart("/albums/{albumId}/photos", sampleAlbumId)
                        .file(file)
                        .param("caption", "Cảnh đẹp"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].caption").value("Cảnh đẹp"));

        verify(mediaService, times(1)).uploadPhotos(eq(sampleAlbumId), any(), eq("Cảnh đẹp"));
    }

    @Test
    @DisplayName("DELETE /photos/{photoId} - Success returns 200")
    void deletePhoto_Success() throws Exception {
        doNothing().when(mediaService).deletePhoto(samplePhotoId);

        mockMvc.perform(delete("/photos/{photoId}", samplePhotoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa ảnh thành công!"));

        verify(mediaService, times(1)).deletePhoto(samplePhotoId);
    }
}
