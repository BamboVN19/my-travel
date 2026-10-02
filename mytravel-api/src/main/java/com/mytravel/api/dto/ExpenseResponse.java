package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ExpenseResponse {
  private UUID id;
  private UUID tripId;
  private UUID paidByUserId;
  private String paidByUserName;
  private BigDecimal amount;
  private String category;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate expenseDate;

  private String paymentMethod;
  private String description;
  private List<ExpenseSplitDto> splits;
}
