package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @Column(nullable = false)
  private String title;

  private String destination;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @Column(name = "start_date")
  private LocalDate startDate;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "total_budget", precision = 15, scale = 2)
  private BigDecimal totalBudget;

  @Builder.Default
  private String status = "PLANNED";

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (status == null) {
      status = "PLANNED";
    }
  }
}
