package com.mytravel.api.controller;

import com.mytravel.api.dto.AlbumPhotoResponse;
import com.mytravel.api.dto.AlbumRequest;
import com.mytravel.api.dto.AlbumResponse;
import com.mytravel.api.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MediaController {

  private final MediaService mediaService;

  @GetMapping("/trips/{tripId}/albums")
  public ResponseEntity<List<AlbumResponse>> getAlbumsByTripId(@PathVariable UUID tripId) {
    List<AlbumResponse> albums = mediaService.getAlbumsByTripId(tripId);
    return ResponseEntity.ok(albums);
  }

  @PostMapping("/trips/{tripId}/albums")
  public ResponseEntity<AlbumResponse> createAlbum(
      @PathVariable UUID tripId,
      @Valid @RequestBody AlbumRequest request) {
    AlbumResponse album = mediaService.createAlbum(tripId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(album);
  }

  @PostMapping("/albums/{albumId}/photos")
  public ResponseEntity<List<AlbumPhotoResponse>> uploadPhotos(
      @PathVariable UUID albumId,
      @RequestParam("files") MultipartFile[] files,
      @RequestParam(value = "caption", required = false) String caption) {
    List<AlbumPhotoResponse> photos = mediaService.uploadPhotos(albumId, files, caption);
    return ResponseEntity.status(HttpStatus.CREATED).body(photos);
  }

  @DeleteMapping("/photos/{photoId}")
  public ResponseEntity<Map<String, String>> deletePhoto(@PathVariable UUID photoId) {
    mediaService.deletePhoto(photoId);
    return ResponseEntity.ok(Map.of("message", "Xóa ảnh thành công!"));
  }
}
