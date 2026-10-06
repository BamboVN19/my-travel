package com.mytravel.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mytravel.api.dto.*;
import com.mytravel.api.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripController Unit Tests")
class TripControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripController tripController;

    private UUID sampleTripId;
    private TripResponse sampleTripResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        sampleTripId = UUID.randomUUID();
        sampleTripResponse = TripResponse.builder()
                .id(sampleTripId)
                .title("Chuyến đi Hạ Long")
                .destination("Hạ Long")
                .startDate(LocalDate.of(2026, 11, 1))
                .endDate(LocalDate.of(2026, 11, 4))
                .totalBudget(new BigDecimal("8000000"))
                .status("PLANNED")
                .role("OWNER")
                .memberCount(1)
                .build();
    }

    @Test
    @DisplayName("POST /trips - Success returns 201")
    void createTrip_Success() throws Exception {
        TripRequest request = TripRequest.builder()
                .title("Chuyến đi Hạ Long")
                .destination("Hạ Long")
                .startDate(LocalDate.of(2026, 11, 1))
                .endDate(LocalDate.of(2026, 11, 4))
                .totalBudget(new BigDecimal("8000000"))
                .build();

        when(tripService.createTrip(any(TripRequest.class))).thenReturn(sampleTripResponse);

        mockMvc.perform(post("/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleTripId.toString()))
                .andExpect(jsonPath("$.title").value("Chuyến đi Hạ Long"));

        verify(tripService, times(1)).createTrip(any(TripRequest.class));
    }

    @Test
    @DisplayName("GET /trips - Success returns PageResponse 200")
    void getTrips_Success() throws Exception {
        PageResponse<TripResponse> pageResponse = PageResponse.<TripResponse>builder()
                .content(List.of(sampleTripResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(tripService.getTrips(any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/trips")
                        .param("status", "PLANNED")
                        .param("search", "Hạ Long"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Chuyến đi Hạ Long"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(tripService, times(1)).getTrips(eq("PLANNED"), eq("Hạ Long"), any());
    }

    @Test
    @DisplayName("GET /trips/current - Returns 200 when current trip exists, 204 when null")
    void getCurrentTrip_Scenarios() throws Exception {
        when(tripService.getCurrentTrip()).thenReturn(sampleTripResponse);

        mockMvc.perform(get("/trips/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Chuyến đi Hạ Long"));

        when(tripService.getCurrentTrip()).thenReturn(null);

        mockMvc.perform(get("/trips/current"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /trips/{id} - Success returns 200")
    void getTripById_Success() throws Exception {
        when(tripService.getTripById(sampleTripId)).thenReturn(sampleTripResponse);

        mockMvc.perform(get("/trips/{id}", sampleTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleTripId.toString()));

        verify(tripService, times(1)).getTripById(sampleTripId);
    }

    @Test
    @DisplayName("PUT /trips/{id} - Success returns 200")
    void updateTrip_Success() throws Exception {
        TripRequest request = TripRequest.builder()
                .title("Chuyến đi Hạ Long Updated")
                .destination("Hạ Long")
                .startDate(LocalDate.of(2026, 11, 2))
                .endDate(LocalDate.of(2026, 11, 5))
                .build();

        when(tripService.updateTrip(eq(sampleTripId), any(TripRequest.class))).thenReturn(sampleTripResponse);

        mockMvc.perform(put("/trips/{id}", sampleTripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(tripService, times(1)).updateTrip(eq(sampleTripId), any(TripRequest.class));
    }

    @Test
    @DisplayName("DELETE /trips/{id} - Success returns 200")
    void deleteTrip_Success() throws Exception {
        doNothing().when(tripService).deleteTrip(sampleTripId);

        mockMvc.perform(delete("/trips/{id}", sampleTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa chuyến đi thành công!"));

        verify(tripService, times(1)).deleteTrip(sampleTripId);
    }

    @Test
    @DisplayName("POST /trips/{id}/members - Success returns 201")
    void addMember_Success() throws Exception {
        AddTripMemberRequest request = AddTripMemberRequest.builder()
                .email("friend@example.com")
                .role("EDITOR")
                .build();

        TripMemberResponse memberResponse = TripMemberResponse.builder()
                .id(UUID.randomUUID())
                .email("friend@example.com")
                .role("EDITOR")
                .build();

        when(tripService.addMember(eq(sampleTripId), any(AddTripMemberRequest.class))).thenReturn(memberResponse);

        mockMvc.perform(post("/trips/{id}/members", sampleTripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("friend@example.com"))
                .andExpect(jsonPath("$.role").value("EDITOR"));

        verify(tripService, times(1)).addMember(eq(sampleTripId), any(AddTripMemberRequest.class));
    }

    @Test
    @DisplayName("GET /trips/{id}/members - Success returns 200")
    void getMembers_Success() throws Exception {
        TripMemberResponse memberResponse = TripMemberResponse.builder()
                .id(UUID.randomUUID())
                .email("friend@example.com")
                .role("EDITOR")
                .build();

        when(tripService.getMembers(sampleTripId)).thenReturn(List.of(memberResponse));

        mockMvc.perform(get("/trips/{id}/members", sampleTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("friend@example.com"));

        verify(tripService, times(1)).getMembers(sampleTripId);
    }

    @Test
    @DisplayName("DELETE /trips/{id}/members/{userId} - Success returns 200")
    void removeMember_Success() throws Exception {
        UUID memberId = UUID.randomUUID();
        doNothing().when(tripService).removeMember(sampleTripId, memberId);

        mockMvc.perform(delete("/trips/{id}/members/{userId}", sampleTripId, memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã xóa thành viên khỏi chuyến đi"));

        verify(tripService, times(1)).removeMember(sampleTripId, memberId);
    }
}
