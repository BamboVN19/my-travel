package com.mytravel.api.service;

import com.mytravel.api.dto.TripRequest;
import com.mytravel.api.dto.TripResponse;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.DuplicateResourceException;
import com.mytravel.api.repository.TripMemberRepository;
import com.mytravel.api.repository.TripRepository;
import com.mytravel.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripMemberRepository tripMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TripService tripService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    void testCreateTrip_StartDateAfterEndDate_ThrowsBadRequestException() {
        when(userService.getCurrentUser()).thenReturn(currentUser);

        TripRequest request = TripRequest.builder()
                .title("Chuyến đi Đà Nẵng")
                .destination("Đà Nẵng")
                .startDate(LocalDate.of(2026, 10, 10))
                .endDate(LocalDate.of(2026, 10, 5))
                .build();

        BadRequestException ex = assertThrows(BadRequestException.class, () -> tripService.createTrip(request));
        assertEquals("Ngày bắt đầu không thể sau ngày kết thúc!", ex.getMessage());
    }

    @Test
    void testCreateTrip_OverlappingDates_ThrowsDuplicateResourceException() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.existsOverlappingTrip(eq(currentUser.getId()), any(), any(), eq(null)))
                .thenReturn(true);

        TripRequest request = TripRequest.builder()
                .title("Chuyến đi Hà Nội")
                .destination("Hà Nội")
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 5))
                .build();

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> tripService.createTrip(request));
        assertTrue(ex.getMessage().contains("Bạn đã có chuyến đi khác trùng trong khoảng thời gian"));
    }

    @Test
    void testCreateTrip_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.existsOverlappingTrip(eq(currentUser.getId()), any(), any(), eq(null)))
                .thenReturn(false);

        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .owner(currentUser)
                .title("Chuyến đi Phú Quốc")
                .destination("Phú Quốc")
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 5))
                .status("PLANNED")
                .build();

        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripRequest request = TripRequest.builder()
                .title("Chuyến đi Phú Quốc")
                .destination("Phú Quốc")
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 5))
                .build();

        TripResponse response = tripService.createTrip(request);
        assertNotNull(response);
        assertEquals("Chuyến đi Phú Quốc", response.getTitle());
    }
}
