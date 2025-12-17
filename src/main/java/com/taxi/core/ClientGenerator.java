package com.taxi.core;

import com.taxi.model.Coordinates;
import com.taxi.model.Order;
import com.taxi.util.Config;
import com.taxi.util.Logger;

import java.util.Random;

public class ClientGenerator implements Runnable {
    private final OrderQueue orderQueue;
    private final Random random;
    private volatile boolean running;
    
    public ClientGenerator(OrderQueue orderQueue) {
        this.orderQueue = orderQueue;
        this.random = new Random();
        this.running = true;
    }
    
    @Override
    public void run() {
        Logger.info("GENERATOR", "Started generating orders");
        
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Coordinates pickup = Coordinates.random(Config.CITY_SIZE);
                Coordinates destination = Coordinates.random(Config.CITY_SIZE);
                
                while (pickup.equals(destination)) {
                    destination = Coordinates.random(Config.CITY_SIZE);
                }
                
                Order order = Order.create(pickup, destination);
                
                Logger.event("GENERATOR", 
                    String.format("New order #%d: %s -> %s (dist: %.1f)", 
                        order.id(), pickup, destination, order.tripDistance()));
                
                orderQueue.addOrder(order);
                
                long delay = Config.ORDER_GENERATION_DELAY_MIN + 
                    random.nextLong(Config.ORDER_GENERATION_DELAY_MAX - Config.ORDER_GENERATION_DELAY_MIN);
                Thread.sleep(delay);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        Logger.info("GENERATOR", "Stopped");
    }
    
    public void stop() {
        this.running = false;
    }
}
