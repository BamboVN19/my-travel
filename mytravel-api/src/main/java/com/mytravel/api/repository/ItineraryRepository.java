package com.mytravel.api.repository;

import com.mytravel.api.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
  List<Itinerary> findByTripIdOrderByDayNumberAscActivityTimeAsc(Long tripId);
}
