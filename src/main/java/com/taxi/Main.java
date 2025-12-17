package com.taxi;

import com.taxi.core.ClientGenerator;
import com.taxi.core.Dispatcher;
import com.taxi.core.OrderQueue;
import com.taxi.service.StatisticsService;
import com.taxi.service.TaxiFleetManager;
import com.taxi.util.Config;
import com.taxi.util.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Logger.separator();
        Logger.info("SYSTEM", "=== AUTONOMOUS TAXI SYSTEM ===");
        Logger.info("SYSTEM", String.format("Taxis: %d | City: %d | Duration: %ds", 
            Config.TAXI_COUNT, Config.CITY_SIZE, Config.SIMULATION_DURATION / 1000));
        Logger.separator();
        
        StatisticsService statistics = new StatisticsService();
        OrderQueue orderQueue = new OrderQueue(Config.ORDER_QUEUE_CAPACITY);
        TaxiFleetManager fleetManager = new TaxiFleetManager(Config.TAXI_COUNT, statistics);
        
        ClientGenerator clientGenerator = new ClientGenerator(orderQueue);
        Dispatcher dispatcher = new Dispatcher(orderQueue, fleetManager.getTaxis());
        
        ExecutorService serviceExecutor = Executors.newFixedThreadPool(2);
        
        try {
            Logger.info("SYSTEM", "Starting components...");
            
            fleetManager.startAll();
            serviceExecutor.submit(dispatcher);
            serviceExecutor.submit(clientGenerator);
            
            Logger.info("SYSTEM", "System running");
            Logger.separator();
            
            Thread.sleep(Config.SIMULATION_DURATION);
            
            Logger.separator();
            Logger.info("SYSTEM", "Shutting down...");
            
            clientGenerator.stop();
            Thread.sleep(2000);
            
            dispatcher.stop();
            fleetManager.stopAll();
            
            serviceExecutor.shutdown();
            serviceExecutor.awaitTermination(5, TimeUnit.SECONDS);
            
            statistics.printFinalReport();
            fleetManager.printStatus();
            
            Logger.info("SYSTEM", "Shutdown complete");
            
        } catch (InterruptedException e) {
            Logger.error("SYSTEM", "Interrupted");
            Thread.currentThread().interrupt();
        } finally {
            serviceExecutor.shutdownNow();
        }
    }
}
