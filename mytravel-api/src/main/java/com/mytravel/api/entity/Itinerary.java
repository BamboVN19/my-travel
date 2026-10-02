package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "itineraries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Itinerary {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Column(name = "day_number", nullable = false)
  private Integer dayNumber;

  @Builder.Default
  @Column(name = "order_index", nullable = false)
  private Integer orderIndex = 0;

  @JsonFormat(pattern = "HH:mm[:ss]")
  @Column(name = "activity_time")
  private LocalTime activityTime;

  @Column(name = "activity_name", nullable = false, length = 200)
  private String activityName;

  @Column(name = "location_name")
  private String locationName;

  @Column(precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(precision = 11, scale = 8)
  private BigDecimal longitude;

  @Column(name = "place_id", columnDefinition = "TEXT")
  private String placeId;

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(name = "image_url", columnDefinition = "TEXT")
  private String imageUrl;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
