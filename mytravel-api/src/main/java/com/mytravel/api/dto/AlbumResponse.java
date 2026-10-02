package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponse {
  private UUID id;
  private UUID tripId;
  private String albumTitle;
  private String description;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime createdAt;

  private List<AlbumPhotoResponse> photos;
}
