package com.mytravel.api.service;

import com.mytravel.api.dto.ItineraryRequest;
import com.mytravel.api.dto.ItineraryResponse;
import com.mytravel.api.entity.Itinerary;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.ItineraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryService {

  private final ItineraryRepository itineraryRepository;
  private final TripService tripService;
  private final UserService userService;

  @Transactional(readOnly = true)
  public List<ItineraryResponse> getItinerariesByTripId(UUID tripId) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripService.findTripById(tripId);
    tripService.checkUserTripAccess(trip, currentUser.getId());

    return itineraryRepository.findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(tripId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public ItineraryResponse createItinerary(UUID tripId, ItineraryRequest request) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripService.findTripById(tripId);
    tripService.checkUserCanEdit(trip, currentUser.getId());

    Itinerary itinerary = Itinerary.builder()
        .trip(trip)
        .dayNumber(request.getDayNumber())
        .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
        .activityTime(request.getActivityTime())
        .activityName(request.getActivityName().trim())
        .locationName(request.getLocationName() != null ? request.getLocationName().trim() : null)
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .placeId(request.getPlaceId())
        .note(request.getNote())
        .imageUrl(request.getImageUrl())
        .build();

    Itinerary saved = itineraryRepository.save(itinerary);
    return mapToResponse(saved);
  }

  @Transactional
  public ItineraryResponse updateItinerary(UUID id, ItineraryRequest request) {
    User currentUser = userService.getCurrentUser();
    Itinerary itinerary = itineraryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mốc lịch trình với ID: " + id));

    tripService.checkUserCanEdit(itinerary.getTrip(), currentUser.getId());

    itinerary.setDayNumber(request.getDayNumber());
    if (request.getOrderIndex() != null) {
      itinerary.setOrderIndex(request.getOrderIndex());
    }
    itinerary.setActivityTime(request.getActivityTime());
    itinerary.setActivityName(request.getActivityName().trim());
    if (request.getLocationName() != null) {
      itinerary.setLocationName(request.getLocationName().trim());
    }
    itinerary.setLatitude(request.getLatitude());
    itinerary.setLongitude(request.getLongitude());
    itinerary.setPlaceId(request.getPlaceId());
    itinerary.setNote(request.getNote());
    if (request.getImageUrl() != null) {
      itinerary.setImageUrl(request.getImageUrl());
    }

    Itinerary updated = itineraryRepository.save(itinerary);
    return mapToResponse(updated);
  }

  @Transactional
  public void deleteItinerary(UUID id) {
    User currentUser = userService.getCurrentUser();
    Itinerary itinerary = itineraryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mốc lịch trình với ID: " + id));

    tripService.checkUserCanEdit(itinerary.getTrip(), currentUser.getId());
    itineraryRepository.delete(itinerary);
  }

  private ItineraryResponse mapToResponse(Itinerary itinerary) {
    return ItineraryResponse.builder()
        .id(itinerary.getId())
        .tripId(itinerary.getTrip().getId())
        .dayNumber(itinerary.getDayNumber())
        .orderIndex(itinerary.getOrderIndex())
        .activityTime(itinerary.getActivityTime())
        .activityName(itinerary.getActivityName())
        .locationName(itinerary.getLocationName())
        .latitude(itinerary.getLatitude())
        .longitude(itinerary.getLongitude())
        .placeId(itinerary.getPlaceId())
        .note(itinerary.getNote())
        .imageUrl(itinerary.getImageUrl())
        .build();
  }
}
