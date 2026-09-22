package com.mytravel.api.controller;

import com.mytravel.api.dto.ItineraryRequest;
import com.mytravel.api.dto.ItineraryResponse;
import com.mytravel.api.service.ItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ItineraryController {

  private final ItineraryService itineraryService;

  @PostMapping("/api/itineraries")
  public ResponseEntity<?> createItinerary(@Valid @RequestBody ItineraryRequest request) {
    try {
      ItineraryResponse response = itineraryService.createItinerary(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/api/trips/{tripId}/itineraries")
  public ResponseEntity<?> getItinerariesByTripId(@PathVariable Long tripId) {
    try {
      List<ItineraryResponse> list = itineraryService.getItinerariesByTripId(tripId);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
