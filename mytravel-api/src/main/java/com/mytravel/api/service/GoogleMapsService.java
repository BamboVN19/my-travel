package com.mytravel.api.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;

import com.mytravel.api.dto.LocationSearchResponse;
import com.mytravel.api.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleMapsService {

  private final GeoApiContext context;
  private final boolean isConfigured;

  public GoogleMapsService(@Value("${google.maps.api-key:}") String apiKey) {
    if (apiKey != null && !apiKey.trim().isEmpty()) {
      this.context = new GeoApiContext.Builder()
          .apiKey(apiKey.trim())
          .build();
      this.isConfigured = true;
      log.info("Google Maps API Service đã được khởi tạo thành công.");
    } else {
      this.context = null;
      this.isConfigured = false;
      log.warn("Google Maps API Key chưa được cấu hình. Các tính năng liên quan đến Google Maps sẽ bị giới hạn.");
    }
  }

  public boolean isConfigured() {
    return isConfigured;
  }

  private void checkApiConfigured() {
    if (!isConfigured || context == null) {
      throw new BadRequestException("Hệ thống chưa được cấu hình GOOGLE_MAPS_API_KEY. Vui lòng kiểm tra lại file application.properties!");
    }
  }

  /**
   * Tìm kiếm địa điểm theo từ khóa (Ví dụ: "Chợ Đêm Đà Lạt", "Bãi biển Mỹ Khê")
   */
  public List<LocationSearchResponse> searchLocations(String query) {
    checkApiConfigured();

    if (query == null || query.trim().isEmpty()) {
      throw new BadRequestException("Từ khóa tìm kiếm địa điểm không được để trống!");
    }

    try {
      // 1. Thử dùng Places API Text Search trước
      PlacesSearchResult[] placesResults = PlacesApi.textSearchQuery(context, query.trim()).await().results;
      if (placesResults != null && placesResults.length > 0) {
        return Arrays.stream(placesResults)
            .map(p -> LocationSearchResponse.builder()
                .placeId(p.placeId)
                .name(p.name)
                .formattedAddress(p.formattedAddress)
                .latitude(p.geometry != null && p.geometry.location != null ? p.geometry.location.lat : null)
                .longitude(p.geometry != null && p.geometry.location != null ? p.geometry.location.lng : null)
                .rating(p.rating)
                .types(p.types != null ? Arrays.asList(p.types) : new ArrayList<>())
                .build())
            .collect(Collectors.toList());
      }

      // 2. Dự phòng bằng Geocoding API nếu Places API không trả về
      GeocodingResult[] geocodingResults = GeocodingApi.geocode(context, query.trim()).await();
      if (geocodingResults != null && geocodingResults.length > 0) {
        return Arrays.stream(geocodingResults)
            .map(g -> LocationSearchResponse.builder()
                .placeId(g.placeId)
                .name(query.trim())
                .formattedAddress(g.formattedAddress)
                .latitude(g.geometry != null && g.geometry.location != null ? g.geometry.location.lat : null)
                .longitude(g.geometry != null && g.geometry.location != null ? g.geometry.location.lng : null)
                .types(g.types != null ? Arrays.stream(g.types).map(AddressType::name).collect(Collectors.toList()) : new ArrayList<>())
                .build())
            .collect(Collectors.toList());
      }

      return new ArrayList<>();
    } catch (Exception e) {
      log.error("Lỗi khi tìm kiếm địa điểm từ Google Maps API: ", e);
      throw new BadRequestException("Không thể tìm kiếm địa điểm từ Google Maps: " + e.getMessage());
    }
  }

  /**
   * Lấy chi tiết thông tin địa điểm theo Place ID
   */
  public LocationSearchResponse getLocationDetails(String placeId) {
    checkApiConfigured();

    if (placeId == null || placeId.trim().isEmpty()) {
      throw new BadRequestException("Place ID không được để trống!");
    }

    try {
      PlaceDetails details = PlacesApi.placeDetails(context, placeId.trim()).await();
      if (details == null) {
        throw new BadRequestException("Không tìm thấy thông tin địa điểm với Place ID: " + placeId);
      }

      return LocationSearchResponse.builder()
          .placeId(details.placeId)
          .name(details.name)
          .formattedAddress(details.formattedAddress)
          .latitude(details.geometry != null && details.geometry.location != null ? details.geometry.location.lat : null)
          .longitude(details.geometry != null && details.geometry.location != null ? details.geometry.location.lng : null)
          .rating(details.rating)
          .types(details.types != null ? Arrays.stream(details.types).map(AddressType::name).collect(Collectors.toList()) : new ArrayList<>())
          .build();
    } catch (Exception e) {
      log.error("Lỗi khi lấy chi tiết địa điểm theo Place ID từ Google Maps API: ", e);
      throw new BadRequestException("Không thể lấy chi tiết địa điểm: " + e.getMessage());
    }
  }

  /**
   * Tra cứu địa chỉ từ tọa độ Latitude và Longitude (Reverse Geocoding)
   */
  public LocationSearchResponse reverseGeocode(double lat, double lng) {
    checkApiConfigured();

    try {
      GeocodingResult[] results = GeocodingApi.reverseGeocode(context, new LatLng(lat, lng)).await();
      if (results != null && results.length > 0) {
        GeocodingResult first = results[0];
        return LocationSearchResponse.builder()
            .placeId(first.placeId)
            .name(first.formattedAddress)
            .formattedAddress(first.formattedAddress)
            .latitude(lat)
            .longitude(lng)
            .types(first.types != null ? Arrays.stream(first.types).map(AddressType::name).collect(Collectors.toList()) : new ArrayList<>())
            .build();
      }
      throw new BadRequestException("Không tìm thấy địa chỉ tương ứng với tọa độ (" + lat + ", " + lng + ")");
    } catch (Exception e) {
      log.error("Lỗi khi giải mã tọa độ ngược từ Google Maps API: ", e);
      throw new BadRequestException("Không thể giải mã tọa độ: " + e.getMessage());
    }
  }
}
