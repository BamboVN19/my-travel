package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "suggested_tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestedTour {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 150)
  private String destination;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "cover_image_url", columnDefinition = "TEXT")
  private String coverImageUrl;

  @Column(name = "duration_days")
  private Integer durationDays;

  @Column(name = "estimated_budget", precision = 15, scale = 2)
  private BigDecimal estimatedBudget;

  @Column(length = 50)
  private String category;

  @Column(precision = 3, scale = 2)
  private BigDecimal rating;

  @Column(name = "review_count")
  private Integer reviewCount;

  @Builder.Default
  @Column(name = "is_active")
  private Boolean isActive = true;

  @OneToMany(mappedBy = "suggestedTour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("dayNumber ASC, orderIndex ASC")
  @Builder.Default
  private List<SuggestedTourStop> stops = new ArrayList<>();

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (isActive == null) {
      isActive = true;
    }
  }
}
