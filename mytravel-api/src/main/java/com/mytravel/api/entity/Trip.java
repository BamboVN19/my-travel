package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User owner;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(nullable = false, length = 150)
  private String destination;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "total_budget", precision = 15, scale = 2)
  private BigDecimal totalBudget;

  @Builder.Default
  @Column(length = 20)
  private String status = "PLANNED";

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
    if (status == null) {
      status = "PLANNED";
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
