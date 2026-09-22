package com.mytravel.api.repository;

import com.mytravel.api.entity.MediaAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaAlbumRepository extends JpaRepository<MediaAlbum, Long> {
  Optional<MediaAlbum> findFirstByTripId(Long tripId);
}
