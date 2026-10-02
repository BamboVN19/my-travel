package com.mytravel.api.repository;

import com.mytravel.api.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

  @EntityGraph(attributePaths = {"owner"})
  @Query(
      value = "SELECT DISTINCT t FROM Trip t LEFT JOIN TripMember tm ON tm.trip = t " +
              "WHERE (t.owner.id = :userId OR tm.user.id = :userId) " +
              "AND (CAST(:status AS string) IS NULL OR LOWER(t.status) = LOWER(CAST(:status AS string))) " +
              "AND (CAST(:search AS string) IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(t.destination) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))",
      countQuery = "SELECT COUNT(DISTINCT t.id) FROM Trip t LEFT JOIN TripMember tm ON tm.trip = t " +
                   "WHERE (t.owner.id = :userId OR tm.user.id = :userId) " +
                   "AND (CAST(:status AS string) IS NULL OR LOWER(t.status) = LOWER(CAST(:status AS string))) " +
                   "AND (CAST(:search AS string) IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(t.destination) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))"
  )
  Page<Trip> searchUserTrips(@Param("userId") UUID userId,
                            @Param("status") String status,
                            @Param("search") String search,
                            Pageable pageable);

  @Query(
      "SELECT COUNT(DISTINCT t) > 0 FROM Trip t LEFT JOIN TripMember tm ON tm.trip = t " +
      "WHERE (t.owner.id = :userId OR tm.user.id = :userId) " +
      "AND LOWER(t.status) != 'cancelled' " +
      "AND (:excludeTripId IS NULL OR t.id != :excludeTripId) " +
      "AND t.startDate <= :endDate AND t.endDate >= :startDate"
  )
  boolean existsOverlappingTrip(
      @Param("userId") UUID userId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("excludeTripId") UUID excludeTripId
  );

  @Query(
      "SELECT DISTINCT t FROM Trip t LEFT JOIN TripMember tm ON tm.trip = t " +
      "WHERE (t.owner.id = :userId OR tm.user.id = :userId) " +
      "AND LOWER(t.status) != 'cancelled' " +
      "AND t.startDate <= :today AND t.endDate >= :today " +
      "ORDER BY t.startDate ASC"
  )
  List<Trip> findCurrentTripsByUserId(@Param("userId") UUID userId, @Param("today") LocalDate today);

  @Modifying
  @Query("UPDATE Trip t SET t.status = 'ONGOING' WHERE LOWER(t.status) = 'planned' AND t.startDate <= :today AND t.endDate >= :today")
  int updateTripStatusToOngoing(@Param("today") LocalDate today);

  @Modifying
  @Query("UPDATE Trip t SET t.status = 'COMPLETED' WHERE LOWER(t.status) IN ('planned', 'ongoing') AND t.endDate < :today")
  int updateTripStatusToCompleted(@Param("today") LocalDate today);

  List<Trip> findByDestinationContainingIgnoreCaseOrTitleContainingIgnoreCase(String destination, String title);

  List<Trip> findByDestinationContainingIgnoreCase(String destination);
}
