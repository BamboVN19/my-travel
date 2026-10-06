package com.mytravel.api.service;

import com.mytravel.api.dto.AlbumRequest;
import com.mytravel.api.dto.AlbumResponse;
import com.mytravel.api.dto.AlbumPhotoResponse;
import com.mytravel.api.entity.AlbumPhoto;
import com.mytravel.api.entity.MediaAlbum;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.AlbumPhotoRepository;
import com.mytravel.api.repository.MediaAlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MediaService Unit Tests")
class MediaServiceTest {

    @Mock
    private MediaAlbumRepository mediaAlbumRepository;

    @Mock
    private AlbumPhotoRepository albumPhotoRepository;

    @Mock
    private TripService tripService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MediaService mediaService;

    private User sampleUser;
    private Trip sampleTrip;
    private MediaAlbum sampleAlbum;
    private AlbumPhoto samplePhoto;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .username("photographer")
                .build();

        sampleTrip = Trip.builder()
                .id(UUID.randomUUID())
                .owner(sampleUser)
                .title("Trip to Phu Quoc")
                .build();

        sampleAlbum = MediaAlbum.builder()
                .id(UUID.randomUUID())
                .trip(sampleTrip)
                .albumTitle("Ảnh Sunset Sanato")
                .description("Ngắm hoàng hôn tuyệt đẹp")
                .createdAt(LocalDateTime.now())
                .build();

        samplePhoto = AlbumPhoto.builder()
                .id(UUID.randomUUID())
                .album(sampleAlbum)
                .photoUrl("/uploads/photos/photo_test.jpg")
                .caption("Hoàng hôn rực rỡ")
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getAlbumsByTripId - Success")
    void getAlbumsByTripId_Success() {
        when(userService.getCurrentUser()).thenReturn(sampleUser);
        when(tripService.findTripById(sampleTrip.getId())).thenReturn(sampleTrip);
        when(mediaAlbumRepository.findByTripIdOrderByCreatedAtDesc(sampleTrip.getId()))
                .thenReturn(List.of(sampleAlbum));
        when(albumPhotoRepository.findByAlbumIdOrderByUploadedAtDesc(sampleAlbum.getId()))
                .thenReturn(List.of(samplePhoto));

        List<AlbumResponse> results = mediaService.getAlbumsByTripId(sampleTrip.getId());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Ảnh Sunset Sanato", results.get(0).getAlbumTitle());
        assertEquals(1, results.get(0).getPhotos().size());
        verify(tripService, times(1)).checkUserTripAccess(sampleTrip, sampleUser.getId());
    }

    @Test
    @DisplayName("createAlbum - Success")
    void createAlbum_Success() {
        when(userService.getCurrentUser()).thenReturn(sampleUser);
        when(tripService.findTripById(sampleTrip.getId())).thenReturn(sampleTrip);
        when(mediaAlbumRepository.save(any(MediaAlbum.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlbumRequest request = AlbumRequest.builder()
                .albumTitle("Ảnh Sunset Sanato")
                .description("Ngắm hoàng hôn tuyệt đẹp")
                .build();

        AlbumResponse response = mediaService.createAlbum(sampleTrip.getId(), request);

        assertNotNull(response);
        assertEquals("Ảnh Sunset Sanato", response.getAlbumTitle());
        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, sampleUser.getId());
        verify(mediaAlbumRepository, times(1)).save(any(MediaAlbum.class));
    }

    @Test
    @DisplayName("uploadPhotos - Success")
    void uploadPhotos_Success() {
        when(userService.getCurrentUser()).thenReturn(sampleUser);
        when(mediaAlbumRepository.findById(sampleAlbum.getId())).thenReturn(Optional.of(sampleAlbum));
        when(albumPhotoRepository.save(any(AlbumPhoto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "files", "sunset.jpg", "image/jpeg", "image bytes content".getBytes()
        );

        List<AlbumPhotoResponse> photos = mediaService.uploadPhotos(
                sampleAlbum.getId(), new MultipartFile[]{file}, "Kỷ niệm đẹp"
        );

        assertNotNull(photos);
        assertEquals(1, photos.size());
        assertEquals("Kỷ niệm đẹp", photos.get(0).getCaption());
        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, sampleUser.getId());
    }

    @Test
    @DisplayName("uploadPhotos - Empty files throws BadRequestException")
    void uploadPhotos_EmptyFiles_ThrowsException() {
        assertThrows(BadRequestException.class, () -> mediaService.uploadPhotos(sampleAlbum.getId(), new MultipartFile[]{}, "caption"));
        assertThrows(BadRequestException.class, () -> mediaService.uploadPhotos(sampleAlbum.getId(), null, "caption"));
    }

    @Test
    @DisplayName("deletePhoto - Success")
    void deletePhoto_Success() {
        when(userService.getCurrentUser()).thenReturn(sampleUser);
        when(albumPhotoRepository.findById(samplePhoto.getId())).thenReturn(Optional.of(samplePhoto));

        mediaService.deletePhoto(samplePhoto.getId());

        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, sampleUser.getId());
        verify(albumPhotoRepository, times(1)).delete(samplePhoto);
    }

    @Test
    @DisplayName("deletePhoto - Not found throws ResourceNotFoundException")
    void deletePhoto_NotFound_ThrowsException() {
        when(userService.getCurrentUser()).thenReturn(sampleUser);
        UUID randomId = UUID.randomUUID();
        when(albumPhotoRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mediaService.deletePhoto(randomId));
    }
}
