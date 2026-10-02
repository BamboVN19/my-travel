package com.mytravel.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"trip_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripMember {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id")
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder.Default
  @Column(length = 20)
  private String role = "MEMBER";

  @Column(name = "joined_at")
  private LocalDateTime joinedAt;

  @PrePersist
  protected void onCreate() {
    if (joinedAt == null) {
      joinedAt = LocalDateTime.now();
    }
  }
}
