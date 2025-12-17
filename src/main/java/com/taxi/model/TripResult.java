package com.taxi.model;

import java.time.Duration;
import java.time.Instant;

public record TripResult(
    long orderId,
    int taxiId,
    double distance,
    Duration waitTime,
    Duration tripDuration,
    Instant completedAt
) {
    public static TripResult create(Order order, int taxiId, double distance, Instant tripStarted) {
        Instant now = Instant.now();
        return new TripResult(
            order.id(),
            taxiId,
            distance,
            Duration.between(order.createdAt(), tripStarted),
            Duration.between(tripStarted, now),
            now
        );
    }
}
