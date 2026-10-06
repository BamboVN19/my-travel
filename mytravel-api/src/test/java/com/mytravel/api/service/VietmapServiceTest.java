package com.mytravel.api.service;

import com.mytravel.api.dto.LocationSearchResponse;
import com.mytravel.api.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("VietmapService Unit Tests")
class VietmapServiceTest {

    private VietmapService service;
    private RestTemplate restTemplateMock;

    @BeforeEach
    void setUp() {
        service = new VietmapService("vietmap_test_api_key");
        restTemplateMock = mock(RestTemplate.class);
        ReflectionTestUtils.setField(service, "restTemplate", restTemplateMock);
    }

    @Test
    @DisplayName("Unconfigured service - isConfigured returns false and methods throw BadRequestException")
    void unconfiguredService_ThrowsBadRequestException() {
        VietmapService unconfigured = new VietmapService("");
        assertFalse(unconfigured.isConfigured());

        assertThrows(BadRequestException.class, () -> unconfigured.searchLocations("Hanoi"));
        assertThrows(BadRequestException.class, () -> unconfigured.getLocationDetails("ref123"));
        assertThrows(BadRequestException.class, () -> unconfigured.reverseGeocode(10.7769, 106.7009));

        VietmapService nullService = new VietmapService(null);
        assertFalse(nullService.isConfigured());
    }

    @Test
    @DisplayName("Configured service - Validates empty inputs")
    void configuredService_ValidatesInputs() {
        assertTrue(service.isConfigured());

        assertThrows(BadRequestException.class, () -> service.searchLocations(""));
        assertThrows(BadRequestException.class, () -> service.searchLocations(null));
        assertThrows(BadRequestException.class, () -> service.getLocationDetails(""));
        assertThrows(BadRequestException.class, () -> service.getLocationDetails(null));
    }

    @Test
    @DisplayName("searchLocations - Returns parsed items and empty items")
    void searchLocations_ReturnsItems() {
        VietmapService.VietmapAutocompleteItem item = new VietmapService.VietmapAutocompleteItem();
        item.setRefId("ref_123");
        item.setName("Hồ Hoàn Kiếm");
        item.setDisplay("Hồ Hoàn Kiếm, Hà Nội");
        item.setAddress("Hà Nội");

        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapAutocompleteItem[].class)))
                .thenReturn(ResponseEntity.ok(new VietmapService.VietmapAutocompleteItem[]{item}));

        List<LocationSearchResponse> results = service.searchLocations("Hoan Kiem");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Hồ Hoàn Kiếm", results.get(0).getName());
        assertEquals("ref_123", results.get(0).getPlaceId());

        // Test empty items
        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapAutocompleteItem[].class)))
                .thenReturn(ResponseEntity.ok(new VietmapService.VietmapAutocompleteItem[]{}));

        List<LocationSearchResponse> emptyResults = service.searchLocations("Unknown");
        assertTrue(emptyResults.isEmpty());
    }

    @Test
    @DisplayName("searchLocations - Handles API exceptions gracefully")
    void searchLocations_HandlesExceptions() {
        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapAutocompleteItem[].class)))
                .thenThrow(new RuntimeException("Vietmap server timeout"));

        assertThrows(BadRequestException.class, () -> service.searchLocations("Nha Trang"));
    }

    @Test
    @DisplayName("getLocationDetails - Returns detail and handles not found")
    void getLocationDetails_SuccessAndNotFound() {
        VietmapService.VietmapPlaceDetail detail = new VietmapService.VietmapPlaceDetail();
        detail.setName("Chợ Bến Thành");
        detail.setDisplay("Chợ Bến Thành, Quận 1");
        detail.setLat(10.7719);
        detail.setLng(106.6983);

        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapPlaceDetail.class)))
                .thenReturn(ResponseEntity.ok(detail));

        LocationSearchResponse resp = service.getLocationDetails("ref_benthanh");

        assertNotNull(resp);
        assertEquals("Chợ Bến Thành", resp.getName());
        assertEquals(10.7719, resp.getLatitude());

        // When body is null
        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapPlaceDetail.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(BadRequestException.class, () -> service.getLocationDetails("ref_null"));

        // When exception occurs
        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapPlaceDetail.class)))
                .thenThrow(new RuntimeException("API error"));

        assertThrows(BadRequestException.class, () -> service.getLocationDetails("ref_err"));
    }

    @Test
    @DisplayName("reverseGeocode - Returns reverse address and handles empty")
    void reverseGeocode_SuccessAndEmpty() {
        VietmapService.VietmapReverseItem item = new VietmapService.VietmapReverseItem();
        item.setRefId("ref_rev_1");
        item.setName("Dinh Độc Lập");
        item.setDisplay("Dinh Độc Lập, TP.HCM");
        item.setLat(10.7770);
        item.setLng(106.6953);

        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapReverseItem[].class)))
                .thenReturn(ResponseEntity.ok(new VietmapService.VietmapReverseItem[]{item}));

        LocationSearchResponse resp = service.reverseGeocode(10.7770, 106.6953);

        assertNotNull(resp);
        assertEquals("Dinh Độc Lập", resp.getName());
        assertEquals("ref_rev_1", resp.getPlaceId());

        // When empty results
        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapReverseItem[].class)))
                .thenReturn(ResponseEntity.ok(new VietmapService.VietmapReverseItem[]{}));

        assertThrows(BadRequestException.class, () -> service.reverseGeocode(1.0, 1.0));

        // When exception
        when(restTemplateMock.getForEntity(anyString(), eq(VietmapService.VietmapReverseItem[].class)))
                .thenThrow(new RuntimeException("Reverse API failure"));

        assertThrows(BadRequestException.class, () -> service.reverseGeocode(2.0, 2.0));
    }
}
