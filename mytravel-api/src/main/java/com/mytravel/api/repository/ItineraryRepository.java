package com.mytravel.api.repository;

import com.mytravel.api.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, UUID> {
  List<Itinerary> findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(UUID tripId);

  List<Itinerary> findByLocationNameContainingIgnoreCaseOrActivityNameContainingIgnoreCase(String locationName, String activityName);

  List<Itinerary> findByTripDestinationContainingIgnoreCase(String destination);
}
