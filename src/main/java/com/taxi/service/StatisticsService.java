package com.taxi.service;

import com.taxi.model.TripResult;
import com.taxi.util.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticsService {
    private final AtomicInteger completedTrips = new AtomicInteger(0);
    private final AtomicLong totalDistance = new AtomicLong(0);
    private final AtomicLong totalWaitTime = new AtomicLong(0);
    private final AtomicLong totalTripTime = new AtomicLong(0);
    private final ConcurrentLinkedQueue<TripResult> recentTrips = new ConcurrentLinkedQueue<>();
    
    public void recordTrip(TripResult result) {
        completedTrips.incrementAndGet();
        totalDistance.addAndGet((long) (result.distance() * 100));
        totalWaitTime.addAndGet(result.waitTime().toMillis());
        totalTripTime.addAndGet(result.tripDuration().toMillis());
        
        recentTrips.add(result);
        while (recentTrips.size() > 100) {
            recentTrips.poll();
        }
        
        if (completedTrips.get() % 5 == 0) {
            printSummary();
        }
    }
    
    public void printSummary() {
        int trips = completedTrips.get();
        if (trips == 0) return;
        
        double avgDistance = (totalDistance.get() / 100.0) / trips;
        long avgWaitTime = totalWaitTime.get() / trips;
        long avgTripTime = totalTripTime.get() / trips;
        
        Logger.info("STATISTICS", String.format(
            "Trips: %d | Avg dist: %.1f | Avg wait: %dms | Avg trip: %dms",
            trips, avgDistance, avgWaitTime, avgTripTime));
    }
    
    public void printFinalReport() {
        Logger.separator();
        Logger.info("STATISTICS", "=== FINAL REPORT ===");
        
        int trips = completedTrips.get();
        if (trips == 0) {
            Logger.info("STATISTICS", "No trips completed");
            return;
        }
        
        Logger.info("STATISTICS", String.format("Total trips: %d", trips));
        Logger.info("STATISTICS", String.format("Total distance: %.1f units", totalDistance.get() / 100.0));
        Logger.info("STATISTICS", String.format("Avg distance: %.1f units", (totalDistance.get() / 100.0) / trips));
        Logger.info("STATISTICS", String.format("Avg wait time: %d ms", totalWaitTime.get() / trips));
        Logger.info("STATISTICS", String.format("Avg trip time: %d ms", totalTripTime.get() / trips));
        Logger.separator();
    }
    
    public int getCompletedTrips() {
        return completedTrips.get();
    }
}
