package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpenseRequest {

  @NotNull(message = "Số tiền không được để trống")
  @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
  private BigDecimal amount;

  @NotBlank(message = "Danh mục không được để trống")
  private String category;

  @NotNull(message = "Ngày chi tiêu không được để trống")
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate expenseDate;

  @Builder.Default
  private String paymentMethod = "CASH";

  private String description;
  private List<ExpenseSplitDto> splits;
}
