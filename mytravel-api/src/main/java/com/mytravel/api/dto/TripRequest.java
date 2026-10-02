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
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripRequest {

  @NotBlank(message = "Tên chuyến đi không được để trống")
  private String title;

  @NotBlank(message = "Điểm đến không được để trống")
  private String destination;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate endDate;

  private BigDecimal totalBudget;
  private String status;
  private UUID ownerId;
}
