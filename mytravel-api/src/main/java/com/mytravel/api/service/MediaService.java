package com.mytravel.api.service;

import com.mytravel.api.dto.MediaUploadResponse;
import com.mytravel.api.entity.AlbumPhoto;
import com.mytravel.api.entity.MediaAlbum;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.repository.AlbumPhotoRepository;
import com.mytravel.api.repository.MediaAlbumRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

  private final MediaAlbumRepository mediaAlbumRepository;
  private final AlbumPhotoRepository albumPhotoRepository;
  private final TripService tripService;

  private final Path uploadLocation = Paths.get("uploads");

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(uploadLocation);
    } catch (IOException e) {
      throw new RuntimeException("Không thể tạo thư mục lưu trữ ảnh!", e);
    }
  }

  public MediaUploadResponse uploadPhoto(MultipartFile file, Long tripId) {
    if (file == null || file.isEmpty()) {
      throw new RuntimeException("File tải lên không được rỗng!");
    }

    Trip trip = tripService.getTripEntityByIdAndValidateUser(tripId);

    // Tìm hoặc tạo MediaAlbum mặc định cho chuyến đi
    MediaAlbum album = mediaAlbumRepository.findFirstByTripId(tripId)
        .orElseGet(() -> {
          MediaAlbum newAlbum = MediaAlbum.builder()
              .trip(trip)
              .albumTitle("Kỷ niệm: " + trip.getTitle())
              .build();
          return mediaAlbumRepository.save(newAlbum);
        });

    try {
      String originalFilename = file.getOriginalFilename();
      String fileExtension = "";
      if (originalFilename != null && originalFilename.contains(".")) {
        fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
      }

      String newFileName = UUID.randomUUID() + fileExtension;
      Path targetPath = uploadLocation.resolve(newFileName);
      Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

      String photoUrl = "/uploads/" + newFileName;

      AlbumPhoto albumPhoto = AlbumPhoto.builder()
          .album(album)
          .photoUrl(photoUrl)
          .caption(originalFilename)
          .build();

      AlbumPhoto savedPhoto = albumPhotoRepository.save(albumPhoto);

      return MediaUploadResponse.builder()
          .id(savedPhoto.getId())
          .tripId(trip.getId())
          .albumId(album.getId())
          .photoUrl(savedPhoto.getPhotoUrl())
          .caption(savedPhoto.getCaption())
          .uploadedAt(savedPhoto.getUploadedAt())
          .build();

    } catch (IOException e) {
      throw new RuntimeException("Lỗi trong quá trình lưu file: " + e.getMessage(), e);
    }
  }
}
