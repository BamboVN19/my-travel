package com.mytravel.api.service;

import com.mytravel.api.dto.ItineraryRequest;
import com.mytravel.api.dto.ItineraryResponse;
import com.mytravel.api.entity.Itinerary;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.ItineraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItineraryService Unit Tests")
class ItineraryServiceTest {

    @Mock
    private ItineraryRepository itineraryRepository;

    @Mock
    private TripService tripService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItineraryService itineraryService;

    private User currentUser;
    private Trip sampleTrip;
    private Itinerary sampleItinerary;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(UUID.randomUUID())
                .username("itinerary_user")
                .build();

        sampleTrip = Trip.builder()
                .id(UUID.randomUUID())
                .owner(currentUser)
                .title("Trip to Sapa")
                .build();

        sampleItinerary = Itinerary.builder()
                .id(UUID.randomUUID())
                .trip(sampleTrip)
                .dayNumber(1)
                .orderIndex(1)
                .activityTime(LocalTime.of(9, 30))
                .activityName("Tham quan Fansipan")
                .locationName("Đỉnh Fansipan")
                .latitude(new BigDecimal("22.3033"))
                .longitude(new BigDecimal("103.7753"))
                .note("Nhớ mang áo ấm")
                .build();
    }

    @Test
    @DisplayName("getItinerariesByTripId - Success")
    void getItinerariesByTripId_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripService.findTripById(sampleTrip.getId())).thenReturn(sampleTrip);
        when(itineraryRepository.findByTripIdOrderByDayNumberAscOrderIndexAscActivityTimeAsc(sampleTrip.getId()))
                .thenReturn(List.of(sampleItinerary));

        List<ItineraryResponse> results = itineraryService.getItinerariesByTripId(sampleTrip.getId());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Tham quan Fansipan", results.get(0).getActivityName());
        verify(tripService, times(1)).checkUserTripAccess(sampleTrip, currentUser.getId());
    }

    @Test
    @DisplayName("createItinerary - Success")
    void createItinerary_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripService.findTripById(sampleTrip.getId())).thenReturn(sampleTrip);
        when(itineraryRepository.save(any(Itinerary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItineraryRequest request = ItineraryRequest.builder()
                .dayNumber(1)
                .orderIndex(1)
                .activityTime(LocalTime.of(9, 30))
                .activityName("Tham quan Fansipan")
                .locationName("Đỉnh Fansipan")
                .latitude(new BigDecimal("22.3033"))
                .longitude(new BigDecimal("103.7753"))
                .note("Nhớ mang áo ấm")
                .build();

        ItineraryResponse response = itineraryService.createItinerary(sampleTrip.getId(), request);

        assertNotNull(response);
        assertEquals("Tham quan Fansipan", response.getActivityName());
        assertEquals(1, response.getDayNumber());
        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, currentUser.getId());
        verify(itineraryRepository, times(1)).save(any(Itinerary.class));
    }

    @Test
    @DisplayName("updateItinerary - Success")
    void updateItinerary_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(itineraryRepository.findById(sampleItinerary.getId())).thenReturn(Optional.of(sampleItinerary));
        when(itineraryRepository.save(any(Itinerary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItineraryRequest request = ItineraryRequest.builder()
                .dayNumber(2)
                .orderIndex(2)
                .activityTime(LocalTime.of(14, 0))
                .activityName("Check-in Bản Cát Cát")
                .locationName("Bản Cát Cát")
                .latitude(new BigDecimal("22.3278"))
                .longitude(new BigDecimal("103.8344"))
                .note("Thuê trang phục dân tộc")
                .imageUrl("https://example.com/catcat.jpg")
                .build();

        ItineraryResponse response = itineraryService.updateItinerary(sampleItinerary.getId(), request);

        assertNotNull(response);
        assertEquals("Check-in Bản Cát Cát", response.getActivityName());
        assertEquals(2, response.getDayNumber());
        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, currentUser.getId());
        verify(itineraryRepository, times(1)).save(sampleItinerary);
    }

    @Test
    @DisplayName("updateItinerary - Not found throws ResourceNotFoundException")
    void updateItinerary_NotFound_ThrowsException() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        UUID randomId = UUID.randomUUID();
        when(itineraryRepository.findById(randomId)).thenReturn(Optional.empty());

        ItineraryRequest request = ItineraryRequest.builder().build();
        assertThrows(ResourceNotFoundException.class, () -> itineraryService.updateItinerary(randomId, request));
    }

    @Test
    @DisplayName("deleteItinerary - Success")
    void deleteItinerary_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(itineraryRepository.findById(sampleItinerary.getId())).thenReturn(Optional.of(sampleItinerary));

        itineraryService.deleteItinerary(sampleItinerary.getId());

        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, currentUser.getId());
        verify(itineraryRepository, times(1)).delete(sampleItinerary);
    }
}
