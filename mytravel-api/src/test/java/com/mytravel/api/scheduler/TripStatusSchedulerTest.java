package com.mytravel.api.scheduler;

import com.mytravel.api.service.TripService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripStatusScheduler Unit Tests")
class TripStatusSchedulerTest {

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripStatusScheduler tripStatusScheduler;

    @Test
    @DisplayName("autoUpdateTripStatuses - Should trigger tripService.updateTripStatuses()")
    void autoUpdateTripStatuses_CallsTripService() {
        tripStatusScheduler.autoUpdateTripStatuses();
        verify(tripService, times(1)).updateTripStatuses();
    }

    @Test
    @DisplayName("onStartup - Should trigger tripService.updateTripStatuses() on ApplicationReadyEvent")
    void onStartup_CallsTripService() {
        tripStatusScheduler.onStartup();
        verify(tripService, times(1)).updateTripStatuses();
    }
}
