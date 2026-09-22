package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {
  @NotNull(message = "tripId không được để trống")
  private Long tripId;

  @NotNull(message = "amount không được để trống")
  private BigDecimal amount;

  private String category;

  private String description;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate expenseDate;

  private String paymentMethod;
}
