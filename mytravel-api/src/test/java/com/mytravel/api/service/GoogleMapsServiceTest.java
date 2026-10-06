package com.mytravel.api.service;

import com.mytravel.api.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GoogleMapsService Unit Tests")
class GoogleMapsServiceTest {

    @Test
    @DisplayName("Unconfigured service - isConfigured returns false and methods throw BadRequestException")
    void unconfiguredService_ThrowsBadRequestException() {
        GoogleMapsService service = new GoogleMapsService("");
        assertFalse(service.isConfigured());

        assertThrows(BadRequestException.class, () -> service.searchLocations("Hanoi"));
        assertThrows(BadRequestException.class, () -> service.getLocationDetails("ChIJ123"));
        assertThrows(BadRequestException.class, () -> service.reverseGeocode(21.0285, 105.8542));

        GoogleMapsService nullService = new GoogleMapsService(null);
        assertFalse(nullService.isConfigured());
    }

    @Test
    @DisplayName("Configured service with dummy key - isConfigured returns true and checks input validation")
    void configuredService_ValidatesInputs() {
        GoogleMapsService service = new GoogleMapsService("AIzaSyFakeKeyForTesting1234567890");
        assertTrue(service.isConfigured());

        assertThrows(BadRequestException.class, () -> service.searchLocations(""));
        assertThrows(BadRequestException.class, () -> service.searchLocations(null));
        assertThrows(BadRequestException.class, () -> service.getLocationDetails(""));
        assertThrows(BadRequestException.class, () -> service.getLocationDetails(null));

        // Methods attempt network call and catch exception safely throwing BadRequestException
        assertThrows(BadRequestException.class, () -> service.searchLocations("Valid Query"));
        assertThrows(BadRequestException.class, () -> service.getLocationDetails("ChIJPlaceId"));
        assertThrows(BadRequestException.class, () -> service.reverseGeocode(21.0285, 105.8542));
    }
}
