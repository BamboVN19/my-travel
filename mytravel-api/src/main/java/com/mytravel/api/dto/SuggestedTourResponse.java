package com.mytravel.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedTourResponse {
  private UUID tripId;
  private String title;
  private String destination;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer durationDays;
  private BigDecimal totalBudget;
  private String status;
  private List<ItineraryResponse> itineraries;
}
