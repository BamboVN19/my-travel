package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryRequest {
  @NotNull(message = "tripId không được để trống")
  private Long tripId;

  private Integer dayNumber;

  @JsonFormat(pattern = "HH:mm[:ss]")
  private LocalTime activityTime;

  private String activityName;

  private String locationName;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private String placeId;

  private String note;
}
