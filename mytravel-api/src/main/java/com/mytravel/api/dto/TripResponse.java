package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
  private UUID id;
  private UUID ownerId;
  private String title;
  private String destination;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate startDate;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate endDate;

  private BigDecimal totalBudget;
  private String status;
  private String role;
  private long memberCount;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime createdAt;
}
