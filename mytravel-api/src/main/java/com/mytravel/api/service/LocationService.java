package com.mytravel.api.service;

import com.mytravel.api.dto.*;
import com.mytravel.api.entity.*;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

  private final SuggestedTourRepository suggestedTourRepository;
  private final SuggestedTourStopRepository suggestedTourStopRepository;
  private final TripRepository tripRepository;
  private final ItineraryRepository itineraryRepository;
  private final TripMemberRepository tripMemberRepository;
  private final VietmapService vietmapService;
  private final GoogleMapsService googleMapsService;
  private final UserService userService;

  /**
   * Tìm kiếm địa điểm với chiến lược ưu tiên dữ liệu trong DB (Suggested Tours & Stops) -> Fallback sang Maps API
   */
  @Transactional(readOnly = true)
  public List<LocationSearchResponse> searchLocations(String query, String provider) {
    String cleanQuery = (query != null) ? query.trim() : "";

    boolean forceExternalProvider = "vietmap".equalsIgnoreCase(provider) || "google".equalsIgnoreCase(provider);

    if (!forceExternalProvider && !cleanQuery.isEmpty()) {
      List<LocationSearchResponse> dbResults = searchLocationsFromDb(cleanQuery);
      if (!dbResults.isEmpty()) {
        log.info("Tìm thấy {} địa điểm gợi ý từ Database cho từ khóa '{}'", dbResults.size(), cleanQuery);
        return dbResults;
      }
      log.info("Không tìm thấy địa điểm phù hợp trong Database cho từ khóa '{}'. Chuyển sang tìm kiếm qua Maps API...", cleanQuery);
    }

    return searchLocationsFromMaps(cleanQuery, provider);
  }

  /**
   * Gợi ý danh sách Tour du lịch mẫu (DB-first từ bảng suggested_tours)
   */
  @Transactional(readOnly = true)
  public List<SuggestedTourResponse> getSuggestedTours(String query) {
    String cleanQuery = (query != null) ? query.trim() : "";
    List<SuggestedTour> tours;

    if (cleanQuery.isEmpty()) {
      tours = suggestedTourRepository.findByIsActiveTrue();
    } else {
      tours = suggestedTourRepository.searchTours(cleanQuery);
    }

    // Nếu bảng suggested_tours trống, fallback sang lấy từ trips trong DB
    if (tours.isEmpty()) {
      return getSuggestedToursFromTrips(cleanQuery);
    }

    return tours.stream().map(this::mapSuggestedTourToResponse).collect(Collectors.toList());
  }

  /**
   * Lấy chi tiết Tour du lịch mẫu theo ID
   */
  @Transactional(readOnly = true)
  public SuggestedTourDto getSuggestedTourById(UUID id) {
    SuggestedTour tour = suggestedTourRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour gợi ý với ID: " + id));

    List<SuggestedTourStop> stops = suggestedTourStopRepository.findBySuggestedTourIdOrderByDayNumberAscOrderIndexAsc(tour.getId());

    List<SuggestedTourStopDto> stopDtos = stops.stream()
        .map(stop -> SuggestedTourStopDto.builder()
            .id(stop.getId())
            .dayNumber(stop.getDayNumber())
            .orderIndex(stop.getOrderIndex())
            .activityTime(stop.getActivityTime())
            .activityName(stop.getActivityName())
            .locationName(stop.getLocationName())
            .latitude(stop.getLatitude())
            .longitude(stop.getLongitude())
            .placeId(stop.getPlaceId())
            .note(stop.getNote())
            .build())
        .collect(Collectors.toList());

    return SuggestedTourDto.builder()
        .id(tour.getId())
        .title(tour.getTitle())
        .destination(tour.getDestination())
        .description(tour.getDescription())
        .coverImageUrl(tour.getCoverImageUrl())
        .durationDays(tour.getDurationDays())
        .estimatedBudget(tour.getEstimatedBudget())
        .category(tour.getCategory())
        .rating(tour.getRating())
        .reviewCount(tour.getReviewCount())
        .stops(stopDtos)
        .build();
  }

  /**
   * Áp dụng (Nhập) Tour gợi ý vào Chuyến đi cá nhân của Người dùng
   */
  @Transactional
  public TripResponse importTourToUserTrip(UUID tourId, ImportTourRequest request) {
    User currentUser = userService.getCurrentUser();
    SuggestedTour tour = suggestedTourRepository.findById(tourId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour gợi ý với ID: " + tourId));

    LocalDate startDate = request.getStartDate();
    int days = (tour.getDurationDays() != null && tour.getDurationDays() > 0) ? tour.getDurationDays() : 1;
    LocalDate endDate = startDate.plusDays(days - 1);

    String title = (request.getCustomTitle() != null && !request.getCustomTitle().trim().isEmpty())
        ? request.getCustomTitle().trim()
        : tour.getTitle();

    // 1. Tạo Trip cá nhân
    Trip trip = Trip.builder()
        .owner(currentUser)
        .title(title)
        .destination(tour.getDestination())
        .startDate(startDate)
        .endDate(endDate)
        .totalBudget(tour.getEstimatedBudget())
        .status("PLANNED")
        .build();

    Trip savedTrip = tripRepository.save(trip);

    // 2. Thêm Owner vào trip_members
    TripMember ownerMember = TripMember.builder()
        .trip(savedTrip)
        .user(currentUser)
        .role("OWNER")
        .build();

    tripMemberRepository.save(ownerMember);

    // 3. Sao chép các mốc lịch trình từ suggested_tour_stops sang itineraries
    List<SuggestedTourStop> stops = suggestedTourStopRepository.findBySuggestedTourIdOrderByDayNumberAscOrderIndexAsc(tour.getId());
    List<Itinerary> itineraries = stops.stream().map(stop -> Itinerary.builder()
        .trip(savedTrip)
        .dayNumber(stop.getDayNumber())
        .orderIndex(stop.getOrderIndex() != null ? stop.getOrderIndex() : 1)
        .activityTime(stop.getActivityTime())
        .activityName(stop.getActivityName())
        .locationName(stop.getLocationName())
        .latitude(stop.getLatitude())
        .longitude(stop.getLongitude())
        .placeId(stop.getPlaceId())
        .note(stop.getNote())
        .build()).collect(Collectors.toList());

    itineraryRepository.saveAll(itineraries);

    log.info("Đã áp dụng Tour '{}' (ID: {}) tạo chuyến đi mới thành công cho user '{}'", tour.getTitle(), tourId, currentUser.getUsername());

    return TripResponse.builder()
        .id(savedTrip.getId())
        .ownerId(currentUser.getId())
        .title(savedTrip.getTitle())
        .destination(savedTrip.getDestination())
        .startDate(savedTrip.getStartDate())
        .endDate(savedTrip.getEndDate())
        .totalBudget(savedTrip.getTotalBudget())
        .status(savedTrip.getStatus())
        .role("OWNER")
        .memberCount(1)
        .createdAt(savedTrip.getCreatedAt())
        .build();
  }

  /**
   * Lấy chi tiết địa điểm theo Place ID
   */
  @Transactional(readOnly = true)
  public LocationSearchResponse getLocationDetails(String placeId, String provider) {
    if (placeId != null && placeId.startsWith("db_tour_")) {
      try {
        UUID tourId = UUID.fromString(placeId.replace("db_tour_", ""));
        return suggestedTourRepository.findById(tourId)
            .map(this::mapSuggestedTourToLocationResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour gợi ý DB với ID: " + tourId));
      } catch (Exception e) {
        log.warn("Lỗi parse DB Tour Place ID: {}", e.getMessage());
      }
    }

    if (placeId != null && placeId.startsWith("db_stop_")) {
      try {
        UUID stopId = UUID.fromString(placeId.replace("db_stop_", ""));
        return suggestedTourStopRepository.findById(stopId)
            .map(this::mapStopToLocationResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Điểm dừng Tour DB với ID: " + stopId));
      } catch (Exception e) {
        log.warn("Lỗi parse DB Stop Place ID: {}", e.getMessage());
      }
    }

    if (placeId != null && placeId.startsWith("db_itin_")) {
      try {
        UUID itinId = UUID.fromString(placeId.replace("db_itin_", ""));
        return itineraryRepository.findById(itinId)
            .map(this::mapItineraryToLocationResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mốc lịch trình DB với ID: " + itinId));
      } catch (Exception e) {
        log.warn("Lỗi parse DB Itinerary Place ID: {}", e.getMessage());
      }
    }

    if (placeId != null && placeId.startsWith("db_trip_")) {
      try {
        UUID tripId = UUID.fromString(placeId.replace("db_trip_", ""));
        return tripRepository.findById(tripId)
            .map(this::mapTripToLocationResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi DB với ID: " + tripId));
      } catch (Exception e) {
        log.warn("Lỗi parse DB Trip Place ID: {}", e.getMessage());
      }
    }

    boolean useVietmap = "vietmap".equalsIgnoreCase(provider) ||
        (provider == null && isVietmapRefId(placeId)) ||
        (provider == null && vietmapService.isConfigured() && !placeId.startsWith("ChIJ"));

    if (useVietmap) {
      try {
        return vietmapService.getLocationDetails(placeId);
      } catch (Exception e) {
        log.warn("Gọi Vietmap Details thất bại, dự phòng Google Maps: {}", e.getMessage());
        if (googleMapsService.isConfigured()) {
          return googleMapsService.getLocationDetails(placeId);
        }
        throw e;
      }
    }

    return googleMapsService.getLocationDetails(placeId);
  }

  private List<LocationSearchResponse> searchLocationsFromDb(String query) {
    Map<String, LocationSearchResponse> resultMap = new LinkedHashMap<>();

    // A. Tìm từ bảng suggested_tours
    List<SuggestedTour> tours = suggestedTourRepository.searchTours(query);
    for (SuggestedTour tour : tours) {
      LocationSearchResponse resp = mapSuggestedTourToLocationResponse(tour);
      resultMap.putIfAbsent(resp.getName().toLowerCase(), resp);
    }

    // B. Tìm từ các điểm dừng suggested_tour_stops
    List<SuggestedTourStop> stops = suggestedTourStopRepository.findByLocationNameContainingIgnoreCase(query);
    for (SuggestedTourStop stop : stops) {
      LocationSearchResponse resp = mapStopToLocationResponse(stop);
      resultMap.putIfAbsent(resp.getName().toLowerCase(), resp);
    }

    // C. Tìm từ các mốc Lịch trình cá nhân (Itinerary)
    List<Itinerary> itineraries = itineraryRepository.findByLocationNameContainingIgnoreCaseOrActivityNameContainingIgnoreCase(query, query);
    for (Itinerary itin : itineraries) {
      LocationSearchResponse resp = mapItineraryToLocationResponse(itin);
      if (resp.getName() != null && !resp.getName().isEmpty()) {
        resultMap.putIfAbsent(resp.getName().toLowerCase(), resp);
      }
    }

    // D. Tìm từ danh sách Chuyến đi cá nhân (Trip)
    List<Trip> trips = tripRepository.findByDestinationContainingIgnoreCaseOrTitleContainingIgnoreCase(query, query);
    for (Trip trip : trips) {
      LocationSearchResponse resp = mapTripToLocationResponse(trip);
      if (resp.getName() != null && !resp.getName().isEmpty()) {
        resultMap.putIfAbsent(resp.getName().toLowerCase(), resp);
      }
    }

    return new ArrayList<>(resultMap.values());
  }

  private List<LocationSearchResponse> searchLocationsFromMaps(String query, String provider) {
    if ("vietmap".equalsIgnoreCase(provider) || (provider == null && vietmapService.isConfigured())) {
      try {
        return vietmapService.searchLocations(query);
      } catch (Exception e) {
        log.warn("Gọi Vietmap Search thất bại, chuyển sang dự phòng Google Maps: {}", e.getMessage());
        if (googleMapsService.isConfigured()) {
          return googleMapsService.searchLocations(query);
        }
        throw e;
      }
    }

    return googleMapsService.searchLocations(query);
  }

  private LocationSearchResponse mapSuggestedTourToLocationResponse(SuggestedTour tour) {
    Double lat = null;
    Double lng = null;

    if (tour.getStops() != null && !tour.getStops().isEmpty()) {
      for (SuggestedTourStop stop : tour.getStops()) {
        if (stop.getLatitude() != null && stop.getLongitude() != null) {
          lat = stop.getLatitude().doubleValue();
          lng = stop.getLongitude().doubleValue();
          break;
        }
      }
    }

    float ratingVal = (tour.getRating() != null) ? tour.getRating().floatValue() : 5.0f;

    return LocationSearchResponse.builder()
        .placeId("db_tour_" + tour.getId())
        .name(tour.getTitle())
        .formattedAddress(tour.getDestination())
        .latitude(lat)
        .longitude(lng)
        .rating(ratingVal)
        .types(List.of("DB_SUGGESTION", "RECOMMENDED_TOUR", tour.getCategory() != null ? tour.getCategory() : "GENERAL"))
        .build();
  }

  private LocationSearchResponse mapStopToLocationResponse(SuggestedTourStop stop) {
    String name = (stop.getLocationName() != null && !stop.getLocationName().isEmpty())
        ? stop.getLocationName() : stop.getActivityName();

    String address = (stop.getSuggestedTour() != null) ? stop.getSuggestedTour().getDestination() : "Việt Nam";

    return LocationSearchResponse.builder()
        .placeId("db_stop_" + stop.getId())
        .name(name)
        .formattedAddress(address)
        .latitude(stop.getLatitude() != null ? stop.getLatitude().doubleValue() : null)
        .longitude(stop.getLongitude() != null ? stop.getLongitude().doubleValue() : null)
        .rating(4.9f)
        .types(List.of("DB_SUGGESTION", "ATTRACTION", "TOUR_TIMELINE"))
        .build();
  }

  private LocationSearchResponse mapItineraryToLocationResponse(Itinerary itin) {
    String name = (itin.getLocationName() != null && !itin.getLocationName().isEmpty())
        ? itin.getLocationName() : itin.getActivityName();

    String address = (itin.getTrip() != null) ? itin.getTrip().getDestination() : "Việt Nam";

    return LocationSearchResponse.builder()
        .placeId("db_itin_" + itin.getId())
        .name(name)
        .formattedAddress(address)
        .latitude(itin.getLatitude() != null ? itin.getLatitude().doubleValue() : null)
        .longitude(itin.getLongitude() != null ? itin.getLongitude().doubleValue() : null)
        .rating(4.9f)
        .types(List.of("DB_SUGGESTION", "ATTRACTION", "USER_TIMELINE"))
        .build();
  }

  private LocationSearchResponse mapTripToLocationResponse(Trip trip) {
    List<Itinerary> itins = itineraryRepository.findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(trip.getId());
    Double lat = null;
    Double lng = null;

    if (!itins.isEmpty()) {
      for (Itinerary itin : itins) {
        if (itin.getLatitude() != null && itin.getLongitude() != null) {
          lat = itin.getLatitude().doubleValue();
          lng = itin.getLongitude().doubleValue();
          break;
        }
      }
    }

    return LocationSearchResponse.builder()
        .placeId("db_trip_" + trip.getId())
        .name(trip.getTitle())
        .formattedAddress(trip.getDestination())
        .latitude(lat)
        .longitude(lng)
        .rating(5.0f)
        .types(List.of("DB_SUGGESTION", "USER_TRIP"))
        .build();
  }

  private SuggestedTourResponse mapSuggestedTourToResponse(SuggestedTour tour) {
    List<SuggestedTourStop> stops = (tour.getStops() != null) ? tour.getStops() : Collections.emptyList();
    List<ItineraryResponse> itinResponses = stops.stream()
        .map(this::mapStopToItineraryResponse)
        .collect(Collectors.toList());

    return SuggestedTourResponse.builder()
        .tripId(tour.getId())
        .title(tour.getTitle())
        .destination(tour.getDestination())
        .startDate(null)
        .endDate(null)
        .durationDays(tour.getDurationDays() != null ? tour.getDurationDays() : 1)
        .totalBudget(tour.getEstimatedBudget())
        .status("RECOMMENDED")
        .itineraries(itinResponses)
        .build();
  }

  private ItineraryResponse mapStopToItineraryResponse(SuggestedTourStop stop) {
    return ItineraryResponse.builder()
        .id(stop.getId())
        .tripId(stop.getSuggestedTour() != null ? stop.getSuggestedTour().getId() : null)
        .dayNumber(stop.getDayNumber())
        .orderIndex(stop.getOrderIndex())
        .activityTime(stop.getActivityTime())
        .activityName(stop.getActivityName())
        .locationName(stop.getLocationName())
        .latitude(stop.getLatitude())
        .longitude(stop.getLongitude())
        .placeId(stop.getPlaceId())
        .note(stop.getNote())
        .build();
  }

  private List<SuggestedTourResponse> getSuggestedToursFromTrips(String cleanQuery) {
    List<Trip> trips;
    if (cleanQuery.isEmpty()) {
      trips = tripRepository.findAll();
    } else {
      trips = tripRepository.findByDestinationContainingIgnoreCaseOrTitleContainingIgnoreCase(cleanQuery, cleanQuery);
    }

    return trips.stream().map(trip -> {
      List<Itinerary> itineraries = itineraryRepository.findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(trip.getId());
      List<ItineraryResponse> itinResponses = itineraries.stream()
          .map(itin -> ItineraryResponse.builder()
              .id(itin.getId())
              .tripId(trip.getId())
              .dayNumber(itin.getDayNumber())
              .orderIndex(itin.getOrderIndex())
              .activityTime(itin.getActivityTime())
              .activityName(itin.getActivityName())
              .locationName(itin.getLocationName())
              .latitude(itin.getLatitude())
              .longitude(itin.getLongitude())
              .placeId(itin.getPlaceId())
              .note(itin.getNote())
              .imageUrl(itin.getImageUrl())
              .build())
          .collect(Collectors.toList());

      int durationDays = 1;
      if (trip.getStartDate() != null && trip.getEndDate() != null) {
        durationDays = (int) ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
      }

      return SuggestedTourResponse.builder()
          .tripId(trip.getId())
          .title(trip.getTitle())
          .destination(trip.getDestination())
          .startDate(trip.getStartDate())
          .endDate(trip.getEndDate())
          .durationDays(durationDays)
          .totalBudget(trip.getTotalBudget())
          .status(trip.getStatus())
          .itineraries(itinResponses)
          .build();
    }).collect(Collectors.toList());
  }

  private boolean isVietmapRefId(String placeId) {
    return placeId != null && (placeId.startsWith("auto:") || placeId.startsWith("vm:") || placeId.startsWith("geocode:"));
  }
}
