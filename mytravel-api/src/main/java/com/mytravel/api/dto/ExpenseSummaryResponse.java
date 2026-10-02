package com.mytravel.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryResponse {
  private UUID tripId;
  private BigDecimal totalBudget;
  private BigDecimal totalSpent;
  private BigDecimal remainingBudget;
  private List<CategoryBreakdownDto> categoryBreakdown;
}
