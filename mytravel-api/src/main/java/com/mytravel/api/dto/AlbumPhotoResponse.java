package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPhotoResponse {
  private UUID id;
  private UUID albumId;
  private String photoUrl;
  private String caption;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime uploadedAt;
}
