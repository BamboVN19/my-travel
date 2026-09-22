package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  @JsonIgnore
  private Trip trip;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal amount;

  @Column(length = 50)
  private String category;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @Column(name = "expense_date")
  private LocalDate expenseDate;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "payment_method", length = 50)
  private String paymentMethod;
}
