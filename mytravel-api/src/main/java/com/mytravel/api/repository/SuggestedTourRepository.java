package com.mytravel.api.repository;

import com.mytravel.api.entity.SuggestedTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuggestedTourRepository extends JpaRepository<SuggestedTour, UUID> {

  List<SuggestedTour> findByIsActiveTrue();

  @Query("SELECT DISTINCT t FROM SuggestedTour t LEFT JOIN FETCH t.stops s WHERE t.isActive = true AND (LOWER(t.destination) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.category) LIKE LOWER(CONCAT('%', :query, '%')))")
  List<SuggestedTour> searchTours(@Param("query") String query);
}
