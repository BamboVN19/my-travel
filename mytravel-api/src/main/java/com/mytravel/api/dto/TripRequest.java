package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequest {
  @NotBlank(message = "Tiêu đề chuyến đi không được để trống")
  private String title;

  private String destination;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate startDate;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate endDate;

  private BigDecimal totalBudget;
}
