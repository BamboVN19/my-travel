package com.mytravel.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mytravel.api.dto.*;
import com.mytravel.api.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
@DisplayName("LocationController Unit Tests")
class LocationControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private UUID sampleTourId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
        sampleTourId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /locations/search - Success returns 200")
    void searchLocations_Success() throws Exception {
        LocationSearchResponse resp = LocationSearchResponse.builder()
                .name("Chợ Bến Thành")
                .formattedAddress("Quận 1, TP Hồ Chí Minh")
                .build();

        when(locationService.searchLocations("Chợ Bến Thành", "auto")).thenReturn(List.of(resp));

        mockMvc.perform(get("/locations/search")
                        .param("query", "Chợ Bến Thành")
                        .param("provider", "auto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chợ Bến Thành"));

        verify(locationService, times(1)).searchLocations("Chợ Bến Thành", "auto");
    }

    @Test
    @DisplayName("GET /locations/tours - Success returns 200")
    void getSuggestedTours_Success() throws Exception {
        SuggestedTourResponse tour = SuggestedTourResponse.builder()
                .tripId(sampleTourId)
                .title("Tour Quy Nhơn")
                .destination("Quy Nhơn")
                .build();

        when(locationService.getSuggestedTours("Quy Nhơn")).thenReturn(List.of(tour));

        mockMvc.perform(get("/locations/tours")
                        .param("query", "Quy Nhơn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tour Quy Nhơn"));

        verify(locationService, times(1)).getSuggestedTours("Quy Nhơn");
    }

    @Test
    @DisplayName("GET /locations/tours/{id} - Success returns 200")
    void getSuggestedTourById_Success() throws Exception {
        SuggestedTourDto dto = SuggestedTourDto.builder()
                .id(sampleTourId)
                .title("Tour Quy Nhơn 3N2Đ")
                .build();

        when(locationService.getSuggestedTourById(sampleTourId)).thenReturn(dto);

        mockMvc.perform(get("/locations/tours/{id}", sampleTourId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tour Quy Nhơn 3N2Đ"));

        verify(locationService, times(1)).getSuggestedTourById(sampleTourId);
    }

    @Test
    @DisplayName("POST /locations/tours/{id}/import - Success returns 201")
    void importTourToUserTrip_Success() throws Exception {
        ImportTourRequest request = ImportTourRequest.builder()
                .startDate(LocalDate.of(2026, 12, 1))
                .customTitle("Quy Nhơn trip")
                .build();

        TripResponse tripResponse = TripResponse.builder()
                .id(UUID.randomUUID())
                .title("Quy Nhơn trip")
                .build();

        when(locationService.importTourToUserTrip(eq(sampleTourId), any(ImportTourRequest.class)))
                .thenReturn(tripResponse);

        mockMvc.perform(post("/locations/tours/{id}/import", sampleTourId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Quy Nhơn trip"));

        verify(locationService, times(1)).importTourToUserTrip(eq(sampleTourId), any(ImportTourRequest.class));
    }

    @Test
    @DisplayName("GET /locations/details/{placeId} - Success returns 200")
    void getLocationDetails_Success() throws Exception {
        LocationSearchResponse resp = LocationSearchResponse.builder()
                .placeId("place_123")
                .name("Kỳ Co Eo Gió")
                .build();

        when(locationService.getLocationDetails("place_123", "vietmap")).thenReturn(resp);

        mockMvc.perform(get("/locations/details/{placeId}", "place_123")
                        .param("provider", "vietmap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kỳ Co Eo Gió"));

        verify(locationService, times(1)).getLocationDetails("place_123", "vietmap");
    }
}
