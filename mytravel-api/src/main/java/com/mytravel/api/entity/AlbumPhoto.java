package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "album_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumPhoto {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "album_id", nullable = false)
  private MediaAlbum album;

  @Column(name = "photo_url", nullable = false, columnDefinition = "TEXT")
  private String photoUrl;

  private String caption;

  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  @Column(name = "uploaded_at")
  private LocalDateTime uploadedAt;

  @PrePersist
  protected void onCreate() {
    if (uploadedAt == null) {
      uploadedAt = LocalDateTime.now();
    }
  }
}
