package com.mytravel.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestedTourDto {
  private UUID id;
  private String title;
  private String destination;
  private String description;
  private String coverImageUrl;
  private Integer durationDays;
  private BigDecimal estimatedBudget;
  private String category;
  private BigDecimal rating;
  private Integer reviewCount;
  private List<SuggestedTourStopDto> stops;
}
