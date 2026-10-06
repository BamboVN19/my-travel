package com.mytravel.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mytravel.api.dto.ItineraryRequest;
import com.mytravel.api.dto.ItineraryResponse;
import com.mytravel.api.service.ItineraryService;
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
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItineraryController Unit Tests")
class ItineraryControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private ItineraryService itineraryService;

    @InjectMocks
    private ItineraryController itineraryController;

    private UUID sampleTripId;
    private UUID sampleItinId;
    private ItineraryResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itineraryController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        sampleTripId = UUID.randomUUID();
        sampleItinId = UUID.randomUUID();

        sampleResponse = ItineraryResponse.builder()
                .id(sampleItinId)
                .tripId(sampleTripId)
                .dayNumber(1)
                .activityName("Tham quan Bà Nà Hills")
                .locationName("Bà Nà Hills")
                .activityTime(LocalTime.of(8, 0))
                .build();
    }

    @Test
    @DisplayName("GET /trips/{tripId}/itineraries - Success returns 200")
    void getItinerariesByTripId_Success() throws Exception {
        when(itineraryService.getItinerariesByTripId(sampleTripId)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/trips/{tripId}/itineraries", sampleTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleItinId.toString()))
                .andExpect(jsonPath("$[0].activityName").value("Tham quan Bà Nà Hills"));

        verify(itineraryService, times(1)).getItinerariesByTripId(sampleTripId);
    }

    @Test
    @DisplayName("POST /trips/{tripId}/itineraries - Success returns 201")
    void createItinerary_Success() throws Exception {
        ItineraryRequest request = ItineraryRequest.builder()
                .dayNumber(1)
                .activityName("Tham quan Bà Nà Hills")
                .locationName("Bà Nà Hills")
                .build();

        when(itineraryService.createItinerary(eq(sampleTripId), any(ItineraryRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/trips/{tripId}/itineraries", sampleTripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleItinId.toString()));

        verify(itineraryService, times(1)).createItinerary(eq(sampleTripId), any(ItineraryRequest.class));
    }

    @Test
    @DisplayName("PUT /itineraries/{id} - Success returns 200")
    void updateItinerary_Success() throws Exception {
        ItineraryRequest request = ItineraryRequest.builder()
                .dayNumber(1)
                .activityName("Tham quan Cầu Vàng Bà Nà Hills")
                .build();

        when(itineraryService.updateItinerary(eq(sampleItinId), any(ItineraryRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/itineraries/{id}", sampleItinId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleItinId.toString()));

        verify(itineraryService, times(1)).updateItinerary(eq(sampleItinId), any(ItineraryRequest.class));
    }

    @Test
    @DisplayName("DELETE /itineraries/{id} - Success returns 200")
    void deleteItinerary_Success() throws Exception {
        doNothing().when(itineraryService).deleteItinerary(sampleItinId);

        mockMvc.perform(delete("/itineraries/{id}", sampleItinId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa mốc lịch trình thành công!"));

        verify(itineraryService, times(1)).deleteItinerary(sampleItinId);
    }
}
