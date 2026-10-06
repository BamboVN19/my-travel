package com.mytravel.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebConfig Unit Tests")
class WebConfigTest {

    @Mock
    private CorsRegistry corsRegistry;

    @Mock
    private CorsRegistration corsRegistration;

    @Mock
    private ResourceHandlerRegistry resourceHandlerRegistry;

    @Mock
    private ResourceHandlerRegistration resourceHandlerRegistration;

    @Test
    @DisplayName("addCorsMappings - Should configure CORS for all endpoints")
    void addCorsMappings_ConfiguresEndpoints() {
        WebConfig webConfig = new WebConfig();
        when(corsRegistry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOriginPatterns("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(true)).thenReturn(corsRegistration);

        webConfig.addCorsMappings(corsRegistry);

        verify(corsRegistry, times(1)).addMapping("/**");
        verify(corsRegistration, times(1)).allowCredentials(true);
    }

    @Test
    @DisplayName("addResourceHandlers - Should configure uploads path")
    void addResourceHandlers_ConfiguresUploadsPath() {
        WebConfig webConfig = new WebConfig();
        when(resourceHandlerRegistry.addResourceHandler("/uploads/**")).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations(anyString())).thenReturn(resourceHandlerRegistration);

        webConfig.addResourceHandlers(resourceHandlerRegistry);

        verify(resourceHandlerRegistry, times(1)).addResourceHandler("/uploads/**");
        verify(resourceHandlerRegistration, times(1)).addResourceLocations(anyString());
    }
}
