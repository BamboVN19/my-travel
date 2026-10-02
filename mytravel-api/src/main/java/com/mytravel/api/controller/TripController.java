package com.mytravel.api.controller;

import com.mytravel.api.dto.*;
import com.mytravel.api.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @PostMapping
  public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest request) {
    TripResponse response = tripService.createTrip(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<PageResponse<TripResponse>> getTrips(
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "search", required = false) String search,
      @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.ASC) Pageable pageable) {
    PageResponse<TripResponse> response = tripService.getTrips(status, search, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/current")
  public ResponseEntity<TripResponse> getCurrentTrip() {
    TripResponse response = tripService.getCurrentTrip();
    if (response == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TripResponse> getTripById(@PathVariable UUID id) {
    TripResponse response = tripService.getTripById(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TripResponse> updateTrip(@PathVariable UUID id, @Valid @RequestBody TripRequest request) {
    TripResponse response = tripService.updateTrip(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deleteTrip(@PathVariable UUID id) {
    tripService.deleteTrip(id);
    return ResponseEntity.ok(Map.of("message", "Xóa chuyến đi thành công!"));
  }

  @PostMapping("/{id}/members")
  public ResponseEntity<TripMemberResponse> addMember(
      @PathVariable UUID id,
      @Valid @RequestBody AddTripMemberRequest request) {
    TripMemberResponse response = tripService.addMember(id, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}/members")
  public ResponseEntity<List<TripMemberResponse>> getMembers(@PathVariable UUID id) {
    List<TripMemberResponse> members = tripService.getMembers(id);
    return ResponseEntity.ok(members);
  }

  @DeleteMapping("/{id}/members/{userId}")
  public ResponseEntity<Map<String, String>> removeMember(@PathVariable UUID id, @PathVariable UUID userId) {
    tripService.removeMember(id, userId);
    return ResponseEntity.ok(Map.of("message", "Đã xóa thành viên khỏi chuyến đi"));
  }
}
