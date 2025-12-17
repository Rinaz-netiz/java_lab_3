package com.taxi.core;

import com.taxi.model.*;
import com.taxi.util.Config;
import com.taxi.util.Logger;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Taxi implements Runnable {
    private final int id;
    private final String name;
    private final AtomicReference<TaxiState> state;
    private final ReentrantLock positionLock;
    private Coordinates currentPosition;
    private volatile Order currentOrder;
    private final Consumer<TripResult> tripCompleteCallback;
    private volatile boolean running;
    
    public Taxi(int id, Coordinates startPosition, Consumer<TripResult> tripCompleteCallback) {
        this.id = id;
        this.name = "TAXI-" + id;
        this.state = new AtomicReference<>(TaxiState.FREE);
        this.positionLock = new ReentrantLock();
        this.currentPosition = startPosition;
        this.tripCompleteCallback = tripCompleteCallback;
        this.running = true;
        Logger.info(name, String.format("Initialized at %s", startPosition));
    }
    
    @Override
    public void run() {
        Logger.info(name, "Started working");
        
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                if (currentOrder == null) {
                    Thread.sleep(100);
                    continue;
                }
                processOrder();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        state.set(TaxiState.OFFLINE);
        Logger.info(name, "Stopped working");
    }
    
    public boolean assignOrder(Order order) {
        if (state.compareAndSet(TaxiState.FREE, TaxiState.HEADING_TO_CLIENT)) {
            this.currentOrder = order;
            Logger.event(name, String.format("Assigned %s", order));
            return true;
        }
        return false;
    }
    
    private void processOrder() throws InterruptedException {
        Order order = this.currentOrder;
        Instant tripStarted = Instant.now();
        
        Logger.info(name, String.format("Heading to pickup at %s", order.pickup()));
        double distanceToClient = getPosition().distanceTo(order.pickup());
        simulateTravel(distanceToClient);
        updatePosition(order.pickup());
        
        state.set(TaxiState.TRANSPORTING);
        Logger.info(name, String.format("Picked up passenger, heading to %s", order.destination()));
        
        double tripDistance = order.tripDistance();
        simulateTravel(tripDistance);
        updatePosition(order.destination());
        
        TripResult result = TripResult.create(order, id, tripDistance, tripStarted);
        Logger.info(name, String.format("Completed trip. Distance: %.1f", tripDistance));
        
        tripCompleteCallback.accept(result);
        
        this.currentOrder = null;
        state.set(TaxiState.FREE);
    }
    
    private void simulateTravel(double distance) throws InterruptedException {
        Thread.sleep((long) (distance * Config.SPEED_FACTOR));
    }
    
    private void updatePosition(Coordinates newPosition) {
        positionLock.lock();
        try {
            this.currentPosition = newPosition;
        } finally {
            positionLock.unlock();
        }
    }
    
    public Coordinates getPosition() {
        positionLock.lock();
        try {
            return currentPosition;
        } finally {
            positionLock.unlock();
        }
    }
    
    public boolean isFree() {
        return state.get() == TaxiState.FREE;
    }
    
    public TaxiState getState() {
        return state.get();
    }
    
    public void stop() {
        this.running = false;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}
