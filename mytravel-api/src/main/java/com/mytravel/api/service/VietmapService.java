package com.mytravel.api.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mytravel.api.dto.LocationSearchResponse;
import com.mytravel.api.exception.BadRequestException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VietmapService {

  private final String apiKey;
  private final boolean isConfigured;
  private final RestTemplate restTemplate;

  public VietmapService(@Value("${vietmap.api-key:}") String apiKey) {
    if (apiKey != null && !apiKey.trim().isEmpty()) {
      this.apiKey = apiKey.trim();
      this.isConfigured = true;
      log.info("Vietmap API Service đã được khởi tạo thành công.");
    } else {
      this.apiKey = null;
      this.isConfigured = false;
      log.warn("Vietmap API Key chưa được cấu hình (vietmap.api-key / VIETMAP_API_KEY).");
    }
    this.restTemplate = new RestTemplate();
  }

  public boolean isConfigured() {
    return isConfigured;
  }

  private void checkApiConfigured() {
    if (!isConfigured) {
      throw new BadRequestException("Hệ thống chưa được cấu hình VIETMAP_API_KEY trong application.properties!");
    }
  }

  /**
   * 1. Tìm kiếm địa điểm theo từ khóa (Vietmap Autocomplete v4 API)
   * URL: https://maps.vietmap.vn/api/autocomplete/v4?apikey={apiKey}&text={query}&display_type=1
   */
  public List<LocationSearchResponse> searchLocations(String query) {
    checkApiConfigured();

    if (query == null || query.trim().isEmpty()) {
      throw new BadRequestException("Từ khóa tìm kiếm địa điểm không được để trống!");
    }

    String url = String.format(
        "https://maps.vietmap.vn/api/autocomplete/v4?apikey=%s&text=%s&display_type=1",
        apiKey, query.trim()
    );

    try {
      ResponseEntity<VietmapAutocompleteItem[]> response = restTemplate.getForEntity(url, VietmapAutocompleteItem[].class);
      VietmapAutocompleteItem[] items = response.getBody();

      if (items != null && items.length > 0) {
        return Arrays.stream(items)
            .map(item -> {
              String fullAddress = item.getDisplay() != null ? item.getDisplay() : item.getAddress();
              String placeName = item.getName() != null && !item.getName().trim().isEmpty() ? item.getName() : fullAddress;
              return LocationSearchResponse.builder()
                  .placeId(item.getRefId())
                  .name(placeName)
                  .formattedAddress(fullAddress)
                  .latitude(null)
                  .longitude(null)
                  .types(List.of("vietmap_autocomplete"))
                  .build();
            })
            .collect(Collectors.toList());
      }
      return new ArrayList<>();
    } catch (Exception e) {
      log.error("Lỗi khi gọi Vietmap Autocomplete v4 API: ", e);
      throw new BadRequestException("Lỗi khi tìm kiếm địa điểm từ Vietmap: " + e.getMessage());
    }
  }

  /**
   * 2. Lấy chi tiết địa điểm theo Place ID / Ref ID (Vietmap Place v4 API)
   * URL: https://maps.vietmap.vn/api/place/v4?apikey={apiKey}&refid={refId}
   */
  public LocationSearchResponse getLocationDetails(String placeId) {
    checkApiConfigured();

    if (placeId == null || placeId.trim().isEmpty()) {
      throw new BadRequestException("Place ID / Ref ID không được để trống!");
    }

    String url = String.format(
        "https://maps.vietmap.vn/api/place/v4?apikey=%s&refid=%s",
        apiKey, placeId.trim()
    );

    try {
      ResponseEntity<VietmapPlaceDetail> response = restTemplate.getForEntity(url, VietmapPlaceDetail.class);
      VietmapPlaceDetail detail = response.getBody();

      if (detail != null) {
        String fullAddress = detail.getDisplay() != null ? detail.getDisplay() : detail.getAddress();
        String placeName = detail.getName() != null && !detail.getName().trim().isEmpty() ? detail.getName() : fullAddress;

        return LocationSearchResponse.builder()
            .placeId(placeId.trim())
            .name(placeName)
            .formattedAddress(fullAddress)
            .latitude(detail.getLat())
            .longitude(detail.getLng())
            .types(List.of("vietmap_place_detail"))
            .build();
      }

      throw new BadRequestException("Vietmap không tìm thấy chi tiết địa điểm với Ref ID: " + placeId);
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      log.error("Lỗi khi gọi Vietmap Place v4 API: ", e);
      throw new BadRequestException("Lỗi khi lấy chi tiết địa điểm từ Vietmap: " + e.getMessage());
    }
  }

  /**
   * 3. Tra cứu địa chỉ từ tọa độ Lat/Lng (Vietmap Reverse Geocoding v4 API)
   * URL: https://maps.vietmap.vn/api/reverse/v4?apikey={apiKey}&lat={lat}&lng={lng}&display_type=1
   */
  public LocationSearchResponse reverseGeocode(double lat, double lng) {
    checkApiConfigured();

    String url = String.format(
        "https://maps.vietmap.vn/api/reverse/v4?apikey=%s&lat=%f&lng=%f&display_type=1",
        apiKey, lat, lng
    );

    try {
      ResponseEntity<VietmapReverseItem[]> response = restTemplate.getForEntity(url, VietmapReverseItem[].class);
      VietmapReverseItem[] items = response.getBody();

      if (items != null && items.length > 0) {
        VietmapReverseItem item = items[0];
        String fullAddress = item.getDisplay() != null ? item.getDisplay() : item.getAddress();
        String placeName = item.getName() != null && !item.getName().trim().isEmpty() ? item.getName() : fullAddress;

        return LocationSearchResponse.builder()
            .placeId(item.getRefId())
            .name(placeName)
            .formattedAddress(fullAddress)
            .latitude(item.getLat() != null ? item.getLat() : lat)
            .longitude(item.getLng() != null ? item.getLng() : lng)
            .types(List.of("vietmap_reverse_geocode"))
            .build();
      }

      throw new BadRequestException("Vietmap không tìm thấy địa chỉ tương ứng với tọa độ (" + lat + ", " + lng + ")");
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      log.error("Lỗi khi gọi Vietmap Reverse Geocoding API: ", e);
      throw new BadRequestException("Lỗi khi giải mã tọa độ từ Vietmap: " + e.getMessage());
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class VietmapAutocompleteItem {
    @JsonProperty("ref_id")
    private String refId;

    private String display;
    private String name;
    private String address;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class VietmapPlaceDetail {
    private String display;
    private String name;
    private String address;

    @JsonProperty("hs_num")
    private String hsNum;

    private String street;
    private String city;
    private String district;
    private String ward;
    private Double lat;
    private Double lng;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class VietmapReverseItem {
    private Double lat;
    private Double lng;

    @JsonProperty("ref_id")
    private String refId;

    private Double distance;
    private String address;
    private String name;
    private String display;
  }
}
