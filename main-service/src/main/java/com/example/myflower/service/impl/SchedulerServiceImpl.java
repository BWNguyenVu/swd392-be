package com.example.myflower.service.impl;

import com.example.myflower.service.FlowerListingService;
import com.example.myflower.service.SchedulerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@EnableScheduling
@Service
public class SchedulerServiceImpl implements SchedulerService {
    private final ConcurrentHashMap<Integer, AtomicInteger> viewCountMap = new ConcurrentHashMap<>();
    private static final Logger LOG = LogManager.getLogger(SchedulerServiceImpl.class);

    @Lazy
    @Autowired
    private FlowerListingService flowerListingService;

    @Override
    public void updateFlowerViews(Integer id, Integer views) {
        viewCountMap.computeIfAbsent(id, k -> new AtomicInteger()).addAndGet(views);
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void saveViewCounts() {
        for (Integer id : viewCountMap.keySet()) {
            AtomicInteger viewCount = viewCountMap.get(id);
            if (viewCount != null) {
                LOG.info("[saveViewCounts] Saving views - id: {}, views: {}", id, viewCount.get());
                flowerListingService.updateViewsFlowerListing(id, viewCount.get());
            }
        }
        viewCountMap.clear();
    }
}
