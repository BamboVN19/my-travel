package com.mytravel.api.service;

import com.mytravel.api.dto.*;
import com.mytravel.api.entity.*;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationService Unit Tests")
class LocationServiceTest {

    @Mock
    private SuggestedTourRepository suggestedTourRepository;

    @Mock
    private SuggestedTourStopRepository suggestedTourStopRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private ItineraryRepository itineraryRepository;

    @Mock
    private TripMemberRepository tripMemberRepository;

    @Mock
    private VietmapService vietmapService;

    @Mock
    private GoogleMapsService googleMapsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LocationService locationService;

    private User sampleUser;
    private SuggestedTour sampleTour;
    private SuggestedTourStop sampleStop;
    private Trip sampleTrip;
    private Itinerary sampleItinerary;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .username("test_explorer")
                .build();

        sampleTour = SuggestedTour.builder()
                .id(UUID.randomUUID())
                .title("Tour Khám Phá Đà Lạt 3N2Đ")
                .destination("Đà Lạt")
                .description("Hành trình trải nghiệm xứ sở ngàn hoa")
                .coverImageUrl("https://example.com/dalat.jpg")
                .durationDays(3)
                .estimatedBudget(new BigDecimal("3500000"))
                .category("Khám phá")
                .rating(new BigDecimal("4.8"))
                .reviewCount(120)
                .isActive(true)
                .build();

        sampleStop = SuggestedTourStop.builder()
                .id(UUID.randomUUID())
                .suggestedTour(sampleTour)
                .dayNumber(1)
                .orderIndex(1)
                .activityTime(LocalTime.of(8, 30))
                .activityName("Check-in Quảng trường Lâm Viên")
                .locationName("Quảng trường Lâm Viên")
                .latitude(new BigDecimal("11.9389"))
                .longitude(new BigDecimal("108.4452"))
                .build();

        sampleTour.setStops(List.of(sampleStop));

        sampleTrip = Trip.builder()
                .id(UUID.randomUUID())
                .owner(sampleUser)
                .title("Chuyến đi Đà Lạt của bạn")
                .destination("Đà Lạt")
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(5))
                .totalBudget(new BigDecimal("5000000"))
                .status("PLANNED")
                .build();

        sampleItinerary = Itinerary.builder()
                .id(UUID.randomUUID())
                .trip(sampleTrip)
                .dayNumber(1)
                .orderIndex(1)
                .activityTime(LocalTime.of(10, 0))
                .activityName("Ăn bánh tráng nướng")
                .locationName("Bánh tráng nướng Dì Đinh")
                .latitude(new BigDecimal("11.9400"))
                .longitude(new BigDecimal("108.4400"))
                .build();
    }

    @Test
    @DisplayName("getSuggestedTours - Returns active tours when query is empty")
    void getSuggestedTours_EmptyQuery_ReturnsActiveTours() {
        when(suggestedTourRepository.findByIsActiveTrue()).thenReturn(List.of(sampleTour));

        List<SuggestedTourResponse> tours = locationService.getSuggestedTours("");

        assertNotNull(tours);
        assertEquals(1, tours.size());
        assertEquals("Tour Khám Phá Đà Lạt 3N2Đ", tours.get(0).getTitle());
    }

    @Test
    @DisplayName("getSuggestedTours - Searches by query when query is present")
    void getSuggestedTours_WithQuery_ReturnsMatchingTours() {
        when(suggestedTourRepository.searchTours("Đà Lạt")).thenReturn(List.of(sampleTour));

        List<SuggestedTourResponse> tours = locationService.getSuggestedTours("Đà Lạt");

        assertNotNull(tours);
        assertEquals(1, tours.size());
        assertEquals("Tour Khám Phá Đà Lạt 3N2Đ", tours.get(0).getTitle());
    }

    @Test
    @DisplayName("getSuggestedTours - Fallback to Trips when suggested_tours is empty")
    void getSuggestedTours_FallbackToTrips() {
        when(suggestedTourRepository.findByIsActiveTrue()).thenReturn(List.of());
        when(tripRepository.findAll()).thenReturn(List.of(sampleTrip));
        when(itineraryRepository.findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(sampleTrip.getId()))
                .thenReturn(List.of(sampleItinerary));

        List<SuggestedTourResponse> tours = locationService.getSuggestedTours("");

        assertNotNull(tours);
        assertEquals(1, tours.size());
        assertEquals(sampleTrip.getTitle(), tours.get(0).getTitle());

        // With non-empty query fallback
        when(suggestedTourRepository.searchTours("Đà Lạt")).thenReturn(List.of());
        when(tripRepository.findByDestinationContainingIgnoreCaseOrTitleContainingIgnoreCase("Đà Lạt", "Đà Lạt"))
                .thenReturn(List.of(sampleTrip));

        List<SuggestedTourResponse> toursQuery = locationService.getSuggestedTours("Đà Lạt");
        assertEquals(1, toursQuery.size());
    }

    @Test
    @DisplayName("getSuggestedTourById - Success")
    void getSuggestedTourById_Success() {
        when(suggestedTourRepository.findById(sampleTour.getId())).thenReturn(Optional.of(sampleTour));
        when(suggestedTourStopRepository.findBySuggestedTourIdOrderByDayNumberAscOrderIndexAsc(sampleTour.getId()))
                .thenReturn(List.of(sampleStop));

        SuggestedTourDto dto = locationService.getSuggestedTourById(sampleTour.getId());

        assertNotNull(dto);
        assertEquals(sampleTour.getTitle(), dto.getTitle());
        assertEquals(1, dto.getStops().size());
        assertEquals("Quảng trường Lâm Viên", dto.getStops().get(0).getLocationName());
    }

    @Test
    @DisplayName("getSuggestedTourById - Not found throws ResourceNotFoundException")
    void getSuggestedTourById_NotFound_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(suggestedTourRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationService.getSuggestedTourById(randomId));
    }

    @Test
    @DisplayName("importTourToUserTrip - Success creates Trip, Owner member, and copies stops")
    void importTourToUserTrip_Success() {
        when(userService.getCurrentUser()).thenReturn(sampleUser);
        when(suggestedTourRepository.findById(sampleTour.getId())).thenReturn(Optional.of(sampleTour));
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(suggestedTourStopRepository.findBySuggestedTourIdOrderByDayNumberAscOrderIndexAsc(sampleTour.getId()))
                .thenReturn(List.of(sampleStop));

        ImportTourRequest request = ImportTourRequest.builder()
                .startDate(LocalDate.now().plusDays(10))
                .customTitle("Chuyến đi Đà Lạt của tôi")
                .build();

        TripResponse response = locationService.importTourToUserTrip(sampleTour.getId(), request);

        assertNotNull(response);
        assertEquals("Chuyến đi Đà Lạt của tôi", response.getTitle());
        assertEquals("Đà Lạt", response.getDestination());
        verify(tripRepository, times(1)).save(any(Trip.class));
        verify(tripMemberRepository, times(1)).save(any(TripMember.class));
        verify(itineraryRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("searchLocations - Returns DB stops and tours when found")
    void searchLocations_DbFirst_Success() {
        when(suggestedTourRepository.searchTours("Đà Lạt")).thenReturn(List.of(sampleTour));
        when(suggestedTourStopRepository.findByLocationNameContainingIgnoreCase("Đà Lạt")).thenReturn(List.of(sampleStop));
        when(itineraryRepository.findByLocationNameContainingIgnoreCaseOrActivityNameContainingIgnoreCase("Đà Lạt", "Đà Lạt"))
                .thenReturn(List.of(sampleItinerary));
        when(tripRepository.findByDestinationContainingIgnoreCaseOrTitleContainingIgnoreCase("Đà Lạt", "Đà Lạt"))
                .thenReturn(List.of(sampleTrip));
        when(itineraryRepository.findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(sampleTrip.getId()))
                .thenReturn(List.of(sampleItinerary));

        List<LocationSearchResponse> results = locationService.searchLocations("Đà Lạt", "auto");

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("searchLocations - Fallback to Vietmap when DB has no match")
    void searchLocations_FallbackMaps_Success() {
        when(suggestedTourRepository.searchTours("Hà Giang")).thenReturn(List.of());
        when(suggestedTourStopRepository.findByLocationNameContainingIgnoreCase("Hà Giang")).thenReturn(List.of());
        when(itineraryRepository.findByLocationNameContainingIgnoreCaseOrActivityNameContainingIgnoreCase("Hà Giang", "Hà Giang")).thenReturn(List.of());
        when(tripRepository.findByDestinationContainingIgnoreCaseOrTitleContainingIgnoreCase("Hà Giang", "Hà Giang")).thenReturn(List.of());

        when(vietmapService.isConfigured()).thenReturn(true);
        LocationSearchResponse mapResp = LocationSearchResponse.builder()
                .name("Cột cờ Lũng Cú")
                .formattedAddress("Đồng Văn, Hà Giang")
                .types(List.of("vietmap"))
                .build();
        when(vietmapService.searchLocations("Hà Giang")).thenReturn(List.of(mapResp));

        List<LocationSearchResponse> results = locationService.searchLocations("Hà Giang", null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Cột cờ Lũng Cú", results.get(0).getName());
    }

    @Test
    @DisplayName("searchLocations - Forced external provider (vietmap and google)")
    void searchLocations_ForcedProvider() {
        LocationSearchResponse resp = LocationSearchResponse.builder().name("Test").build();

        // Forced vietmap
        when(vietmapService.searchLocations("Hue")).thenReturn(List.of(resp));
        List<LocationSearchResponse> resVietmap = locationService.searchLocations("Hue", "vietmap");
        assertEquals(1, resVietmap.size());

        // Forced google
        when(googleMapsService.searchLocations("Hue")).thenReturn(List.of(resp));
        List<LocationSearchResponse> resGoogle = locationService.searchLocations("Hue", "google");
        assertEquals(1, resGoogle.size());
    }

    @Test
    @DisplayName("getLocationDetails - All DB Place ID prefixes (tour, stop, itin, trip)")
    void getLocationDetails_AllDbPrefixes() {
        // 1. db_tour_
        when(suggestedTourRepository.findById(sampleTour.getId())).thenReturn(Optional.of(sampleTour));
        LocationSearchResponse respTour = locationService.getLocationDetails("db_tour_" + sampleTour.getId(), "auto");
        assertNotNull(respTour);
        assertEquals(sampleTour.getTitle(), respTour.getName());

        // 2. db_stop_
        when(suggestedTourStopRepository.findById(sampleStop.getId())).thenReturn(Optional.of(sampleStop));
        LocationSearchResponse respStop = locationService.getLocationDetails("db_stop_" + sampleStop.getId(), "auto");
        assertNotNull(respStop);
        assertEquals(sampleStop.getLocationName(), respStop.getName());

        // 3. db_itin_
        when(itineraryRepository.findById(sampleItinerary.getId())).thenReturn(Optional.of(sampleItinerary));
        LocationSearchResponse respItin = locationService.getLocationDetails("db_itin_" + sampleItinerary.getId(), "auto");
        assertNotNull(respItin);
        assertEquals(sampleItinerary.getLocationName(), respItin.getName());

        // 4. db_trip_
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));
        when(itineraryRepository.findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(sampleTrip.getId()))
                .thenReturn(List.of(sampleItinerary));
        LocationSearchResponse respTrip = locationService.getLocationDetails("db_trip_" + sampleTrip.getId(), "auto");
        assertNotNull(respTrip);
        assertEquals(sampleTrip.getTitle(), respTrip.getName());
    }

    @Test
    @DisplayName("getLocationDetails - External Maps (Vietmap with RefId and Google Maps fallback)")
    void getLocationDetails_ExternalMaps() {
        LocationSearchResponse externalResp = LocationSearchResponse.builder().name("External Place").build();

        // Vietmap RefId
        when(vietmapService.getLocationDetails("vm:12345")).thenReturn(externalResp);
        LocationSearchResponse resp1 = locationService.getLocationDetails("vm:12345", null);
        assertNotNull(resp1);

        // Fallback to Google Maps when Vietmap fails
        when(vietmapService.getLocationDetails("auto:fail")).thenThrow(new RuntimeException("Vietmap error"));
        when(googleMapsService.isConfigured()).thenReturn(true);
        when(googleMapsService.getLocationDetails("auto:fail")).thenReturn(externalResp);

        LocationSearchResponse respFallback = locationService.getLocationDetails("auto:fail", "vietmap");
        assertNotNull(respFallback);
    }
}
