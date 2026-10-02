package com.mytravel.api.repository;

import com.mytravel.api.entity.SuggestedTourStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuggestedTourStopRepository extends JpaRepository<SuggestedTourStop, UUID> {

  List<SuggestedTourStop> findBySuggestedTourIdOrderByDayNumberAscOrderIndexAsc(UUID tourId);

  List<SuggestedTourStop> findByLocationNameContainingIgnoreCase(String locationName);
}
