package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAlbum {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Column(name = "album_title", nullable = false, length = 150)
  private String albumTitle;

  @Column(columnDefinition = "TEXT")
  private String description;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
