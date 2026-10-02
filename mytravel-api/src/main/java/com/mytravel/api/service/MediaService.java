package com.mytravel.api.service;

import com.mytravel.api.dto.AlbumPhotoResponse;
import com.mytravel.api.dto.AlbumRequest;
import com.mytravel.api.dto.AlbumResponse;
import com.mytravel.api.entity.AlbumPhoto;
import com.mytravel.api.entity.MediaAlbum;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.AlbumPhotoRepository;
import com.mytravel.api.repository.MediaAlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

  private final MediaAlbumRepository mediaAlbumRepository;
  private final AlbumPhotoRepository albumPhotoRepository;
  private final TripService tripService;
  private final UserService userService;

  @Transactional(readOnly = true)
  public List<AlbumResponse> getAlbumsByTripId(UUID tripId) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripService.findTripById(tripId);
    tripService.checkUserTripAccess(trip, currentUser.getId());

    List<MediaAlbum> albums = mediaAlbumRepository.findByTripIdOrderByCreatedAtDesc(tripId);
    return albums.stream()
        .map(this::mapToAlbumResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public AlbumResponse createAlbum(UUID tripId, AlbumRequest request) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripService.findTripById(tripId);
    tripService.checkUserCanEdit(trip, currentUser.getId());

    MediaAlbum album = MediaAlbum.builder()
        .trip(trip)
        .albumTitle(request.getAlbumTitle().trim())
        .description(request.getDescription())
        .build();

    MediaAlbum saved = mediaAlbumRepository.save(album);
    return mapToAlbumResponse(saved);
  }

  @Transactional
  public List<AlbumPhotoResponse> uploadPhotos(UUID albumId, MultipartFile[] files, String caption) {
    if (files == null || files.length == 0) {
      throw new BadRequestException("Danh sách file không được để trống");
    }

    User currentUser = userService.getCurrentUser();
    MediaAlbum album = mediaAlbumRepository.findById(albumId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Album với ID: " + albumId));

    tripService.checkUserCanEdit(album.getTrip(), currentUser.getId());

    List<AlbumPhoto> savedPhotos = new ArrayList<>();
    try {
      Path uploadDir = Paths.get("uploads", "photos").toAbsolutePath().normalize();
      Files.createDirectories(uploadDir);

      for (MultipartFile file : files) {
        if (file.isEmpty()) continue;

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
          extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = "photo_album_" + albumId + "_" + UUID.randomUUID() + extension;
        Path filePath = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String photoUrl = "/uploads/photos/" + fileName;

        AlbumPhoto photo = AlbumPhoto.builder()
            .album(album)
            .photoUrl(photoUrl)
            .caption(caption)
            .build();

        savedPhotos.add(albumPhotoRepository.save(photo));
      }
    } catch (IOException e) {
      log.error("Lỗi khi lưu ảnh album: ", e);
      throw new BadRequestException("Lỗi khi lưu ảnh vào hệ thống: " + e.getMessage());
    }

    return savedPhotos.stream()
        .map(this::mapToPhotoResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public void deletePhoto(UUID photoId) {
    User currentUser = userService.getCurrentUser();
    AlbumPhoto photo = albumPhotoRepository.findById(photoId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh với ID: " + photoId));

    tripService.checkUserCanEdit(photo.getAlbum().getTrip(), currentUser.getId());
    albumPhotoRepository.delete(photo);
  }

  private AlbumResponse mapToAlbumResponse(MediaAlbum album) {
    List<AlbumPhoto> photos = albumPhotoRepository.findByAlbumIdOrderByUploadedAtDesc(album.getId());
    List<AlbumPhotoResponse> photoResponses = photos.stream()
        .map(this::mapToPhotoResponse)
        .collect(Collectors.toList());

    return AlbumResponse.builder()
        .id(album.getId())
        .tripId(album.getTrip().getId())
        .albumTitle(album.getAlbumTitle())
        .description(album.getDescription())
        .createdAt(album.getCreatedAt())
        .photos(photoResponses)
        .build();
  }

  private AlbumPhotoResponse mapToPhotoResponse(AlbumPhoto photo) {
    return AlbumPhotoResponse.builder()
        .id(photo.getId())
        .albumId(photo.getAlbum().getId())
        .photoUrl(photo.getPhotoUrl())
        .caption(photo.getCaption())
        .uploadedAt(photo.getUploadedAt())
        .build();
  }
}
