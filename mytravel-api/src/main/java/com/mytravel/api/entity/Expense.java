package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id")
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "paid_by_user_id")
  private User paidByUser;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 50)
  private String category;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @Column(name = "expense_date", nullable = false)
  private LocalDate expenseDate;

  @Builder.Default
  @Column(name = "payment_method", length = 50)
  private String paymentMethod = "CASH";

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
