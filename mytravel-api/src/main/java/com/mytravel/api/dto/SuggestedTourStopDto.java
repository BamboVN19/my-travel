package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestedTourStopDto {
  private UUID id;
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
}
