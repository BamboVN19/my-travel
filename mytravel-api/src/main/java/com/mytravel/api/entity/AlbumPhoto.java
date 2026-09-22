package com.mytravel.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "album_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumPhoto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "album_id", nullable = false)
  @JsonIgnore
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
