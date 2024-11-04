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
    @NonNull
    private StorageService storageService;

    @Scheduled(fixedDelayString = "P1D")
    public void clearFlowerCache() {
        flowerListingService.clearFlowerListingCache();
    }

    @Scheduled(cron = "0 */15 * * * *") // Runs every 15 minutes
    public void disableExpiredFlower() {
        flowerListingService.disableExpiredFlowers();
    }

    @Scheduled(fixedDelayString = "P3D") //Run daily
    public void clearPresignedUrlCache() {storageService.clearPresignedUrlCache();}
}
