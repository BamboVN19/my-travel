package com.mytravel.api.controller;

import com.mytravel.api.dto.TripRequest;
import com.mytravel.api.dto.TripResponse;
import com.mytravel.api.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @PostMapping
  public ResponseEntity<?> createTrip(@Valid @RequestBody TripRequest request) {
    try {
      TripResponse response = tripService.createTrip(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping
  public ResponseEntity<?> getAllTrips() {
    try {
      List<TripResponse> trips = tripService.getAllTrips();
      return ResponseEntity.ok(trips);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getTripById(@PathVariable Long id) {
    try {
      TripResponse trip = tripService.getTripById(id);
      return ResponseEntity.ok(trip);
    } catch (Exception e) {
      return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTrip(@PathVariable Long id) {
    try {
      tripService.deleteTrip(id);
      return ResponseEntity.ok(Map.of("message", "Xóa chuyến đi thành công!"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
