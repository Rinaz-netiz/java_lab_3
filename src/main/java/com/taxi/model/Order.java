package com.taxi.model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public record Order(long id, Coordinates pickup, Coordinates destination, Instant createdAt) {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    
    public static Order create(Coordinates pickup, Coordinates destination) {
        return new Order(ID_GENERATOR.getAndIncrement(), pickup, destination, Instant.now());
    }
    
    public double tripDistance() {
        return pickup.distanceTo(destination);
    }
    
    @Override
    public String toString() {
        return String.format("Order#%d[%s -> %s]", id, pickup, destination);
    }
}
