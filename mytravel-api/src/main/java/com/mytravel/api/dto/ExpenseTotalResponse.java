package com.mytravel.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseTotalResponse {
  private Long tripId;
  private BigDecimal totalExpense;
}
