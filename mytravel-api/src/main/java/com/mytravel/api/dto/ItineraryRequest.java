package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItineraryRequest {

  @NotNull(message = "Day number không được để trống")
  private Integer dayNumber;

  @Builder.Default
  private Integer orderIndex = 0;

  @JsonFormat(pattern = "HH:mm[:ss]")
  private LocalTime activityTime;

  @NotBlank(message = "Tên hoạt động không được để trống")
  private String activityName;

  private String locationName;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private String placeId;
  private String note;
  private String imageUrl;
}
