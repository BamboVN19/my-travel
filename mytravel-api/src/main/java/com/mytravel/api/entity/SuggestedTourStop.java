package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "suggested_tour_stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestedTourStop {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id", nullable = false)
  @JsonIgnore
  @ToString.Exclude
  private SuggestedTour suggestedTour;

  @Column(name = "day_number", nullable = false)
  private Integer dayNumber;

  @Column(name = "order_index")
  private Integer orderIndex;

  @Column(name = "activity_time")
  private LocalTime activityTime;

  @Column(name = "activity_name", nullable = false, length = 200)
  private String activityName;

  @Column(name = "location_name", length = 255)
  private String locationName;

  @Column(precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(precision = 11, scale = 8)
  private BigDecimal longitude;

  @Column(name = "place_id", columnDefinition = "TEXT")
  private String placeId;

  @Column(columnDefinition = "TEXT")
  private String note;
}
