package com.mytravel.api.controller;

import com.mytravel.api.dto.*;
import com.mytravel.api.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  /**
   * 1. Tìm kiếm địa điểm theo từ khóa
   * Ưu tiên tìm trong Database trước (Tour gợi ý & Mốc lịch trình). 
   * Nếu DB không có kết quả mới gọi sang Maps API (Vietmap / Google Maps).
   * Query params: query (Required), provider (Optional: vietmap | google | db)
   */
  @GetMapping("/search")
  public ResponseEntity<List<LocationSearchResponse>> searchLocations(
      @RequestParam String query,
      @RequestParam(required = false) String provider) {
    return ResponseEntity.ok(locationService.searchLocations(query, provider));
  }

  /**
   * 2. Gợi ý danh sách Tour du lịch từ Database
   * Trả về danh sách Tour gợi ý mẫu kèm lịch trình dừng chân theo ngày cho người dùng lựa chọn.
   * Query params: query (Optional: "Đà Lạt", "Hà Giang", "Hội An",...)
   */
  @GetMapping("/tours")
  public ResponseEntity<List<SuggestedTourResponse>> getSuggestedTours(
      @RequestParam(required = false) String query) {
    return ResponseEntity.ok(locationService.getSuggestedTours(query));
  }

  /**
   * 3. Lấy thông tin chi tiết của một Tour gợi ý theo ID
   */
  @GetMapping("/tours/{id}")
  public ResponseEntity<SuggestedTourDto> getSuggestedTourById(@PathVariable UUID id) {
    return ResponseEntity.ok(locationService.getSuggestedTourById(id));
  }

  /**
   * 4. Áp dụng (Nhập) Tour gợi ý vào Chuyến đi cá nhân của Người dùng
   */
  @PostMapping("/tours/{id}/import")
  public ResponseEntity<TripResponse> importTourToUserTrip(
      @PathVariable UUID id,
      @Valid @RequestBody ImportTourRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(locationService.importTourToUserTrip(id, request));
  }

  /**
   * 5. Lấy chi tiết địa điểm theo Place ID
   * Hỗ trợ Place ID trong DB (db_tour_xxx, db_stop_xxx, db_itin_xxx, db_trip_xxx) và Maps (Google / Vietmap)
   */
  @GetMapping("/details/{placeId}")
  public ResponseEntity<LocationSearchResponse> getLocationDetails(
      @PathVariable String placeId,
      @RequestParam(required = false) String provider) {
    return ResponseEntity.ok(locationService.getLocationDetails(placeId, provider));
  }
}
