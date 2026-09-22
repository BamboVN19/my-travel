package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "itineraries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Itinerary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  @JsonIgnore
  private Trip trip;

  @Column(name = "day_number")
  private Integer dayNumber;

  @JsonFormat(pattern = "HH:mm[:ss]")
  @Column(name = "activity_time")
  private LocalTime activityTime;

  @Column(name = "activity_name")
  private String activityName;

  @Column(name = "location_name")
  private String locationName;

  @Column(precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(precision = 11, scale = 8)
  private BigDecimal longitude;

  @Column(name = "place_id")
  private String placeId;

  @Column(columnDefinition = "TEXT")
  private String note;
}
