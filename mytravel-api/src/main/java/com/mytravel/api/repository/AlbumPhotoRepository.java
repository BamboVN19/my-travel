package com.mytravel.api.repository;

import com.mytravel.api.entity.AlbumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumPhotoRepository extends JpaRepository<AlbumPhoto, UUID> {
  List<AlbumPhoto> findByAlbumIdOrderByUploadedAtDesc(UUID albumId);
}
