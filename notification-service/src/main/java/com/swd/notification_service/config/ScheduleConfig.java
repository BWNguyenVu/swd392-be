package com.swd.notification_service.config;

import com.swd.notification_service.services.BroadcastNotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {
    @NonNull
    private BroadcastNotificationService broadcastNotificationService;

    @Scheduled(cron = "0 * * * * *") // Runs at the start of every minute
    public void runEveryMinute() {
        broadcastNotificationService.checkForBroadcastNotification();
    }
}
