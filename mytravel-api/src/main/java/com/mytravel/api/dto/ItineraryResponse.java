package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryResponse {
  private UUID id;
  private UUID tripId;
  private Integer dayNumber;
  private Integer orderIndex;

  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime activityTime;

  private String activityName;
  private String locationName;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private String placeId;
  private String note;
  private String imageUrl;
}
