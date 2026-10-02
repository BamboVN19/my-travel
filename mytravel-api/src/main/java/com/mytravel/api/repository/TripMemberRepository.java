package com.mytravel.api.repository;

import com.mytravel.api.entity.TripMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, UUID> {

  @EntityGraph(attributePaths = {"user"})
  List<TripMember> findByTripId(UUID tripId);

  Optional<TripMember> findByTripIdAndUserId(UUID tripId, UUID userId);

  boolean existsByTripIdAndUserId(UUID tripId, UUID userId);

  void deleteByTripIdAndUserId(UUID tripId, UUID userId);

  long countByTripId(UUID tripId);
}
