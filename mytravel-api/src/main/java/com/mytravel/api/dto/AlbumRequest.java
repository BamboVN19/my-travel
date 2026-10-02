package com.mytravel.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumRequest {

  @NotBlank(message = "Tiêu đề Album không được để trống")
  private String albumTitle;

  private String description;
}
