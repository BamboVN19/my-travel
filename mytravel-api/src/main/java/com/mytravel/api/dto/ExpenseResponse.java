package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
  private Long id;
  private Long tripId;
  private BigDecimal amount;
  private String category;
  private String description;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate expenseDate;

  private String paymentMethod;
}
