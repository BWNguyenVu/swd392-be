package com.example.myflower.config;

import com.example.myflower.service.FileMediaService;
import com.example.myflower.service.FlowerListingService;
import com.example.myflower.service.StorageService;
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
    private FlowerListingService flowerListingService;
    private StorageService storageService;

    @Scheduled(cron = "0 0 0 0 * ?")
    public void clearFlowerCache() {
        flowerListingService.clearFlowerListingCache();
    }

    @Scheduled(cron = "0 */15 * * * *") // Runs every 15 minutes
    public void disableExpiredFlower() {
        flowerListingService.disableExpiredFlowers();
    }

    @Scheduled(cron = "0 0 0 0 * ?") //Run daily
    public void clearPresignedUrlCache() {storageService.clearPresignedUrlCache();}
}
