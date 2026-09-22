package com.mytravel.api.controller;

import com.mytravel.api.dto.MediaUploadResponse;
import com.mytravel.api.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

  private final MediaService mediaService;

  @PostMapping("/upload")
  public ResponseEntity<?> uploadPhoto(
      @RequestParam("file") MultipartFile file,
      @RequestParam("tripId") Long tripId) {
    try {
      MediaUploadResponse response = mediaService.uploadPhoto(file, tripId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
