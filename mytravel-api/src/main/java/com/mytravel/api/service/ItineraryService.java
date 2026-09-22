package com.mytravel.api.service;

import com.mytravel.api.dto.ItineraryRequest;
import com.mytravel.api.dto.ItineraryResponse;
import com.mytravel.api.entity.Itinerary;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.repository.ItineraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryService {

  private final ItineraryRepository itineraryRepository;
  private final TripService tripService;

  public ItineraryResponse createItinerary(ItineraryRequest request) {
    Trip trip = tripService.getTripEntityByIdAndValidateUser(request.getTripId());

    Itinerary itinerary = Itinerary.builder()
        .trip(trip)
        .dayNumber(request.getDayNumber())
        .activityTime(request.getActivityTime())
        .activityName(request.getActivityName())
        .locationName(request.getLocationName())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .placeId(request.getPlaceId())
        .note(request.getNote())
        .build();

    Itinerary saved = itineraryRepository.save(itinerary);
    return mapToResponse(saved);
  }

  public List<ItineraryResponse> getItinerariesByTripId(Long tripId) {
    // Validate quyền truy cập của user với chuyến đi này
    tripService.getTripEntityByIdAndValidateUser(tripId);

    return itineraryRepository.findByTripIdOrderByDayNumberAscActivityTimeAsc(tripId)
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  private ItineraryResponse mapToResponse(Itinerary item) {
    return ItineraryResponse.builder()
        .id(item.getId())
        .tripId(item.getTrip() != null ? item.getTrip().getId() : null)
        .dayNumber(item.getDayNumber())
        .activityTime(item.getActivityTime())
        .activityName(item.getActivityName())
        .locationName(item.getLocationName())
        .latitude(item.getLatitude())
        .longitude(item.getLongitude())
        .placeId(item.getPlaceId())
        .note(item.getNote())
        .build();
  }
}
