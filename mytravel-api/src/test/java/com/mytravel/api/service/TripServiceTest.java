package com.mytravel.api.service;

import com.mytravel.api.dto.*;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.TripMember;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.DuplicateResourceException;
import com.mytravel.api.exception.ForbiddenException;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.TripMemberRepository;
import com.mytravel.api.repository.TripRepository;
import com.mytravel.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripService Unit Tests")
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
    private User otherUser;
    private Trip sampleTrip;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .fullName("Nguyen Van Test")
                .email("test@example.com")
                .build();

        otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("otheruser")
                .fullName("Tran Van Other")
                .email("other@example.com")
                .build();

        sampleTrip = Trip.builder()
                .id(UUID.randomUUID())
                .owner(currentUser)
                .title("Chuyến đi Phú Quốc")
                .destination("Phú Quốc")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(10))
                .totalBudget(new BigDecimal("10000000"))
                .status("PLANNED")
                .build();
    }

    @Test
    @DisplayName("updateTripStatuses - Success")
    void updateTripStatuses_Success() {
        when(tripRepository.updateTripStatusToOngoing(any(LocalDate.class))).thenReturn(2);
        when(tripRepository.updateTripStatusToCompleted(any(LocalDate.class))).thenReturn(3);

        tripService.updateTripStatuses();

        verify(tripRepository, times(1)).updateTripStatusToOngoing(any(LocalDate.class));
        verify(tripRepository, times(1)).updateTripStatusToCompleted(any(LocalDate.class));
    }

    @Test
    @DisplayName("createTrip - StartDate after EndDate throws BadRequestException")
    void createTrip_StartDateAfterEndDate_ThrowsBadRequestException() {
        when(userService.getCurrentUser()).thenReturn(currentUser);

        TripRequest request = TripRequest.builder()
                .title("Chuyến đi Đà Nẵng")
                .destination("Đà Nẵng")
                .startDate(LocalDate.of(2026, 10, 10))
                .endDate(LocalDate.of(2026, 10, 5))
                .build();

        assertThrows(BadRequestException.class, () -> tripService.createTrip(request));
    }

    @Test
    @DisplayName("createTrip - Overlapping dates throws DuplicateResourceException")
    void createTrip_OverlappingDates_ThrowsDuplicateResourceException() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.existsOverlappingTrip(eq(currentUser.getId()), any(), any(), eq(null)))
                .thenReturn(true);

        TripRequest request = TripRequest.builder()
                .title("Chuyến đi Hà Nội")
                .destination("Hà Nội")
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 5))
                .build();

        assertThrows(DuplicateResourceException.class, () -> tripService.createTrip(request));
    }

    @Test
    @DisplayName("createTrip - Success with PLANNED / ONGOING / COMPLETED status calculation")
    void createTrip_Success_CalculatesStatus() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.existsOverlappingTrip(eq(currentUser.getId()), any(), any(), eq(null)))
                .thenReturn(false);
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test 1: Future dates -> PLANNED
        TripRequest reqFuture = TripRequest.builder()
                .title("Chuyến đi tương lai")
                .destination("Đà Lạt")
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(5))
                .totalBudget(new BigDecimal("5000000"))
                .build();
        TripResponse resFuture = tripService.createTrip(reqFuture);
        assertEquals("PLANNED", resFuture.getStatus());

        // Test 2: Current dates -> ONGOING
        TripRequest reqOngoing = TripRequest.builder()
                .title("Chuyến đi hiện tại")
                .destination("Đà Lạt")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .build();
        TripResponse resOngoing = tripService.createTrip(reqOngoing);
        assertEquals("ONGOING", resOngoing.getStatus());

        // Test 3: Past dates -> COMPLETED
        TripRequest reqPast = TripRequest.builder()
                .title("Chuyến đi quá khứ")
                .destination("Đà Lạt")
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(5))
                .build();
        TripResponse resPast = tripService.createTrip(reqPast);
        assertEquals("COMPLETED", resPast.getStatus());

        // Test 4: Custom requested status e.g. CANCELLED
        TripRequest reqCustom = TripRequest.builder()
                .title("Chuyến đi hủy")
                .destination("Đà Lạt")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .status("CANCELLED")
                .build();
        TripResponse resCustom = tripService.createTrip(reqCustom);
        assertEquals("CANCELLED", resCustom.getStatus());
    }

    @Test
    @DisplayName("getTrips - Success returns PageResponse")
    void getTrips_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Trip> tripPage = new PageImpl<>(List.of(sampleTrip), pageable, 1);

        when(tripRepository.searchUserTrips(eq(currentUser.getId()), eq("PLANNED"), eq("Phú Quốc"), eq(pageable)))
                .thenReturn(tripPage);
        when(tripMemberRepository.countByTripId(sampleTrip.getId())).thenReturn(1L);

        PageResponse<TripResponse> response = tripService.getTrips("PLANNED", "Phú Quốc", pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("OWNER", response.getContent().get(0).getRole());
    }

    @Test
    @DisplayName("getTripById - Success for Owner")
    void getTripById_Owner_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));
        when(tripMemberRepository.countByTripId(sampleTrip.getId())).thenReturn(1L);

        TripResponse response = tripService.getTripById(sampleTrip.getId());

        assertNotNull(response);
        assertEquals(sampleTrip.getTitle(), response.getTitle());
        assertEquals("OWNER", response.getRole());
    }

    @Test
    @DisplayName("getTripById - Success for Member (Viewer/Editor)")
    void getTripById_Member_Success() {
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));
        when(tripMemberRepository.existsByTripIdAndUserId(sampleTrip.getId(), otherUser.getId())).thenReturn(true);

        TripMember member = TripMember.builder()
                .trip(sampleTrip)
                .user(otherUser)
                .role("EDITOR")
                .build();
        when(tripMemberRepository.findByTripIdAndUserId(sampleTrip.getId(), otherUser.getId())).thenReturn(Optional.of(member));
        when(tripMemberRepository.countByTripId(sampleTrip.getId())).thenReturn(2L);

        TripResponse response = tripService.getTripById(sampleTrip.getId());

        assertNotNull(response);
        assertEquals("EDITOR", response.getRole());
    }

    @Test
    @DisplayName("getTripById - Unauthorized user throws ForbiddenException")
    void getTripById_Unauthorized_ThrowsForbiddenException() {
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));
        when(tripMemberRepository.existsByTripIdAndUserId(sampleTrip.getId(), otherUser.getId())).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> tripService.getTripById(sampleTrip.getId()));
    }

    @Test
    @DisplayName("getCurrentTrip - When trip exists and when no trip")
    void getCurrentTrip_Scenarios() {
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // When trips exist
        when(tripRepository.findCurrentTripsByUserId(eq(currentUser.getId()), any(LocalDate.class)))
                .thenReturn(List.of(sampleTrip));
        when(tripMemberRepository.countByTripId(sampleTrip.getId())).thenReturn(1L);

        TripResponse res = tripService.getCurrentTrip();
        assertNotNull(res);
        assertEquals(sampleTrip.getTitle(), res.getTitle());

        // When no current trip
        when(tripRepository.findCurrentTripsByUserId(eq(currentUser.getId()), any(LocalDate.class)))
                .thenReturn(List.of());
        assertNull(tripService.getCurrentTrip());
    }

    @Test
    @DisplayName("updateTrip - Success for Owner")
    void updateTrip_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));
        when(tripRepository.existsOverlappingTrip(eq(currentUser.getId()), any(), any(), eq(sampleTrip.getId())))
                .thenReturn(false);
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TripRequest request = TripRequest.builder()
                .title("Chuyến đi cập nhật")
                .destination("Nha Trang")
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(15))
                .totalBudget(new BigDecimal("15000000"))
                .build();

        TripResponse response = tripService.updateTrip(sampleTrip.getId(), request);

        assertNotNull(response);
        assertEquals("Chuyến đi cập nhật", response.getTitle());
        assertEquals("Nha Trang", response.getDestination());
    }

    @Test
    @DisplayName("updateTrip - Dates invalid or overlapping throws exception")
    void updateTrip_InvalidDates_ThrowsException() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));

        // StartDate after EndDate
        TripRequest reqInvalidDates = TripRequest.builder()
                .title("Fail trip")
                .destination("Nha Trang")
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(5))
                .build();
        assertThrows(BadRequestException.class, () -> tripService.updateTrip(sampleTrip.getId(), reqInvalidDates));

        // Overlapping trip
        TripRequest reqOverlap = TripRequest.builder()
                .title("Fail trip")
                .destination("Nha Trang")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .build();
        when(tripRepository.existsOverlappingTrip(eq(currentUser.getId()), any(), any(), eq(sampleTrip.getId())))
                .thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> tripService.updateTrip(sampleTrip.getId(), reqOverlap));
    }

    @Test
    @DisplayName("updateTrip - Viewer member cannot edit trip")
    void updateTrip_Viewer_ThrowsForbiddenException() {
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));

        TripMember viewer = TripMember.builder()
                .trip(sampleTrip)
                .user(otherUser)
                .role("VIEWER")
                .build();
        when(tripMemberRepository.findByTripIdAndUserId(sampleTrip.getId(), otherUser.getId())).thenReturn(Optional.of(viewer));

        TripRequest req = TripRequest.builder()
                .title("Try to update")
                .destination("Nha Trang")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .build();

        assertThrows(ForbiddenException.class, () -> tripService.updateTrip(sampleTrip.getId(), req));
    }

    @Test
    @DisplayName("deleteTrip - Only Owner can delete trip")
    void deleteTrip_OwnerAndNonOwner() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));

        // Owner deletes successfully
        tripService.deleteTrip(sampleTrip.getId());
        verify(tripRepository, times(1)).delete(sampleTrip);

        // Non-owner attempts to delete
        when(userService.getCurrentUser()).thenReturn(otherUser);
        assertThrows(ForbiddenException.class, () -> tripService.deleteTrip(sampleTrip.getId()));
    }

    @Test
    @DisplayName("addMember - Success")
    void addMember_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripMemberRepository.existsByTripIdAndUserId(sampleTrip.getId(), otherUser.getId())).thenReturn(false);
        when(tripRepository.existsOverlappingTrip(otherUser.getId(), sampleTrip.getStartDate(), sampleTrip.getEndDate(), sampleTrip.getId()))
                .thenReturn(false);
        when(tripMemberRepository.save(any(TripMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddTripMemberRequest request = AddTripMemberRequest.builder()
                .email("other@example.com")
                .role("EDITOR")
                .build();

        TripMemberResponse response = tripService.addMember(sampleTrip.getId(), request);

        assertNotNull(response);
        assertEquals("EDITOR", response.getRole());
        assertEquals("otheruser", response.getUsername());
    }

    @Test
    @DisplayName("addMember - User not found, already member, or overlapping trip throws exceptions")
    void addMember_ErrorCases() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));

        // 1. Email not found
        AddTripMemberRequest reqNotFound = AddTripMemberRequest.builder().email("missing@example.com").build();
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripService.addMember(sampleTrip.getId(), reqNotFound));

        // 2. Already member
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripMemberRepository.existsByTripIdAndUserId(sampleTrip.getId(), otherUser.getId())).thenReturn(true);
        AddTripMemberRequest reqAlready = AddTripMemberRequest.builder().email("other@example.com").build();
        assertThrows(DuplicateResourceException.class, () -> tripService.addMember(sampleTrip.getId(), reqAlready));

        // 3. Member has overlapping trip
        when(tripMemberRepository.existsByTripIdAndUserId(sampleTrip.getId(), otherUser.getId())).thenReturn(false);
        when(tripRepository.existsOverlappingTrip(otherUser.getId(), sampleTrip.getStartDate(), sampleTrip.getEndDate(), sampleTrip.getId()))
                .thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> tripService.addMember(sampleTrip.getId(), reqAlready));
    }

    @Test
    @DisplayName("removeMember - Success and removing Owner forbidden")
    void removeMember_Scenarios() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));

        // Remove regular member
        tripService.removeMember(sampleTrip.getId(), otherUser.getId());
        verify(tripMemberRepository, times(1)).deleteByTripIdAndUserId(sampleTrip.getId(), otherUser.getId());

        // Attempt to remove owner
        assertThrows(ForbiddenException.class, () -> tripService.removeMember(sampleTrip.getId(), currentUser.getId()));
    }

    @Test
    @DisplayName("getMembers - Success returns member list")
    void getMembers_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripRepository.findById(sampleTrip.getId())).thenReturn(Optional.of(sampleTrip));

        TripMember member = TripMember.builder()
                .trip(sampleTrip)
                .user(currentUser)
                .role("OWNER")
                .build();
        when(tripMemberRepository.findByTripId(sampleTrip.getId())).thenReturn(List.of(member));

        List<TripMemberResponse> members = tripService.getMembers(sampleTrip.getId());

        assertEquals(1, members.size());
        assertEquals("OWNER", members.get(0).getRole());
    }

    @Test
    @DisplayName("findTripById - Not found throws ResourceNotFoundException")
    void findTripById_NotFound_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(tripRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tripService.findTripById(randomId));
    }
}
