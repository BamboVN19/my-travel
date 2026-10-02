package com.mytravel.api.scheduler;

import com.mytravel.api.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripStatusScheduler {

  private final TripService tripService;

  /**
   * Run automatically every hour to update trip statuses based on current date.
   */
  @Scheduled(cron = "0 0 * * * *")
  public void autoUpdateTripStatuses() {
    log.info("Starting automatic trip status update task...");
    tripService.updateTripStatuses();
  }

  /**
   * Run on application startup to ensure database status is synchronized immediately.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onStartup() {
    log.info("Updating trip statuses on application startup...");
    tripService.updateTripStatuses();
  }
}
