package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponse {
  private Long id;
  private Long userId;
  private String title;
  private String destination;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate startDate;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate endDate;

  private BigDecimal totalBudget;
  private String status;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime createdAt;
}
