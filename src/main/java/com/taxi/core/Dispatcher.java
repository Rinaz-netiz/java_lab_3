package com.taxi.core;

import com.taxi.model.Coordinates;
import com.taxi.model.Order;
import com.taxi.util.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Dispatcher implements Runnable {
    private final OrderQueue orderQueue;
    private final List<Taxi> taxis;
    private volatile boolean running;
    private final Object taxiSelectionLock = new Object();
    
    public Dispatcher(OrderQueue orderQueue, List<Taxi> taxis) {
        this.orderQueue = orderQueue;
        this.taxis = taxis;
        this.running = true;
    }
    
    @Override
    public void run() {
        Logger.info("DISPATCHER", "Started");
        
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Order order = orderQueue.takeOrder(500, TimeUnit.MILLISECONDS);
                
                if (order == null) {
                    continue;
                }
                
                boolean assigned = assignOrderToTaxi(order);
                
                if (!assigned) {
                    Logger.warn("DISPATCHER", 
                        String.format("No free taxi for order #%d, returning to queue", order.id()));
                    orderQueue.addOrder(order);
                    Thread.sleep(200);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        Logger.info("DISPATCHER", "Stopped");
    }
    
    private boolean assignOrderToTaxi(Order order) {
        synchronized (taxiSelectionLock) {
            Optional<Taxi> bestTaxi = findNearestFreeTaxi(order.pickup());
            
            if (bestTaxi.isPresent()) {
                Taxi taxi = bestTaxi.get();
                if (taxi.assignOrder(order)) {
                    double distance = taxi.getPosition().distanceTo(order.pickup());
                    Logger.info("DISPATCHER", 
                        String.format("Assigned order #%d to %s (dist: %.1f)", 
                            order.id(), taxi.getName(), distance));
                    return true;
                }
            }
            
            return false;
        }
    }
    
    private Optional<Taxi> findNearestFreeTaxi(Coordinates pickup) {
        return taxis.stream()
            .filter(Taxi::isFree)
            .min(Comparator.comparingDouble(taxi -> taxi.getPosition().distanceTo(pickup)));
    }
    
    public void stop() {
        this.running = false;
    }
}
