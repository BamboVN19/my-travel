package com.mytravel.api.service;

import com.mytravel.api.dto.TripRequest;
import com.mytravel.api.dto.TripResponse;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.User;
import com.mytravel.api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final UserService userService;

  public TripResponse createTrip(TripRequest request) {
    User currentUser = userService.getCurrentUser();

    Trip trip = Trip.builder()
        .user(currentUser)
        .title(request.getTitle())
        .destination(request.getDestination())
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .totalBudget(request.getTotalBudget())
        .status("PLANNED")
        .build();

    Trip saved = tripRepository.save(trip);
    return mapToResponse(saved);
  }

  public List<TripResponse> getAllTrips() {
    User currentUser = userService.getCurrentUser();
    return tripRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public TripResponse getTripById(Long id) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripRepository.findByIdAndUserId(id, currentUser.getId())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi hoặc bạn không có quyền truy cập!"));
    return mapToResponse(trip);
  }

  public void deleteTrip(Long id) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripRepository.findByIdAndUserId(id, currentUser.getId())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi hoặc bạn không có quyền xóa!"));
    tripRepository.delete(trip);
  }

  public Trip getTripEntityByIdAndValidateUser(Long tripId) {
    User currentUser = userService.getCurrentUser();
    return tripRepository.findByIdAndUserId(tripId, currentUser.getId())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi hoặc bạn không có quyền truy cập!"));
  }

  private TripResponse mapToResponse(Trip trip) {
    return TripResponse.builder()
        .id(trip.getId())
        .userId(trip.getUser() != null ? trip.getUser().getId() : null)
        .title(trip.getTitle())
        .destination(trip.getDestination())
        .startDate(trip.getStartDate())
        .endDate(trip.getEndDate())
        .totalBudget(trip.getTotalBudget())
        .status(trip.getStatus())
        .createdAt(trip.getCreatedAt())
        .build();
  }
}
