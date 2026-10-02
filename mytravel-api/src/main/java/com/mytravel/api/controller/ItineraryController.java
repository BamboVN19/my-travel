package com.mytravel.api.controller;

import com.mytravel.api.dto.ItineraryRequest;
import com.mytravel.api.dto.ItineraryResponse;
import com.mytravel.api.service.ItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ItineraryController {

  private final ItineraryService itineraryService;

  @GetMapping("/trips/{tripId}/itineraries")
  public ResponseEntity<List<ItineraryResponse>> getItinerariesByTripId(@PathVariable UUID tripId) {
    List<ItineraryResponse> list = itineraryService.getItinerariesByTripId(tripId);
    return ResponseEntity.ok(list);
  }

  @PostMapping("/trips/{tripId}/itineraries")
  public ResponseEntity<ItineraryResponse> createItinerary(
      @PathVariable UUID tripId,
      @Valid @RequestBody ItineraryRequest request) {
    ItineraryResponse response = itineraryService.createItinerary(tripId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/itineraries/{id}")
  public ResponseEntity<ItineraryResponse> updateItinerary(
      @PathVariable UUID id,
      @Valid @RequestBody ItineraryRequest request) {
    ItineraryResponse response = itineraryService.updateItinerary(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/itineraries/{id}")
  public ResponseEntity<Map<String, String>> deleteItinerary(@PathVariable UUID id) {
    itineraryService.deleteItinerary(id);
    return ResponseEntity.ok(Map.of("message", "Xóa mốc lịch trình thành công!"));
  }
}
