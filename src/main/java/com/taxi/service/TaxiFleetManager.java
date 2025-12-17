package com.taxi.service;

import com.taxi.core.Taxi;
import com.taxi.model.Coordinates;
import com.taxi.model.TripResult;
import com.taxi.util.Config;
import com.taxi.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TaxiFleetManager {
    private final List<Taxi> taxis;
    private final ExecutorService taxiExecutor;
    private final StatisticsService statisticsService;
    
    public TaxiFleetManager(int taxiCount, StatisticsService statisticsService) {
        this.taxis = new CopyOnWriteArrayList<>();
        this.taxiExecutor = Executors.newFixedThreadPool(taxiCount);
        this.statisticsService = statisticsService;
        initializeTaxis(taxiCount);
    }
    
    private void initializeTaxis(int count) {
        Consumer<TripResult> tripCallback = result -> {
            statisticsService.recordTrip(result);
            Logger.event("FLEET_MGR", 
                String.format("Trip #%d by TAXI-%d completed", result.orderId(), result.taxiId()));
        };
        
        for (int i = 1; i <= count; i++) {
            Coordinates startPos = Coordinates.random(Config.CITY_SIZE);
            Taxi taxi = new Taxi(i, startPos, tripCallback);
            taxis.add(taxi);
        }
        
        Logger.info("FLEET_MGR", String.format("Initialized %d taxis", count));
    }
    
    public void startAll() {
        for (Taxi taxi : taxis) {
            taxiExecutor.submit(taxi);
        }
        Logger.info("FLEET_MGR", "All taxis started");
    }
    
    public void stopAll() {
        Logger.info("FLEET_MGR", "Stopping all taxis...");
        
        for (Taxi taxi : taxis) {
            taxi.stop();
        }
        
        taxiExecutor.shutdown();
        try {
            if (!taxiExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                taxiExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            taxiExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        Logger.info("FLEET_MGR", "All taxis stopped");
    }
    
    public List<Taxi> getTaxis() {
        return new ArrayList<>(taxis);
    }
    
    public void printStatus() {
        Logger.info("FLEET_MGR", "=== TAXI STATUS ===");
        for (Taxi taxi : taxis) {
            Logger.info(taxi.getName(), 
                String.format("Position: %s | State: %s", taxi.getPosition(), taxi.getState().getDescription()));
        }
    }
}
