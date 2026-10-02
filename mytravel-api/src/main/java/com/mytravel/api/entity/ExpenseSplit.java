package com.mytravel.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "expense_splits", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"expense_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplit {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "expense_id", nullable = false)
  private Expense expense;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "split_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal splitAmount;

  @Builder.Default
  @Column(name = "is_settled", nullable = false)
  private boolean isSettled = false;
}
