package com.mytravel.api.repository;

import com.mytravel.api.entity.MediaAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaAlbumRepository extends JpaRepository<MediaAlbum, UUID> {
  List<MediaAlbum> findByTripIdOrderByCreatedAtDesc(UUID tripId);
}
