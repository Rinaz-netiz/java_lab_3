package com.taxi.core;

import com.taxi.model.Order;
import com.taxi.util.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OrderQueue {
    private final BlockingQueue<Order> queue;
    
    public OrderQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }
    
    public boolean addOrder(Order order) {
        try {
            boolean added = queue.offer(order, 5, TimeUnit.SECONDS);
            if (added) {
                Logger.event("ORDER_QUEUE", 
                    String.format("Order #%d added. Queue size: %d", order.id(), queue.size()));
            } else {
                Logger.warn("ORDER_QUEUE", 
                    String.format("Failed to add order #%d - queue full", order.id()));
            }
            return added;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public Order takeOrder(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
    
    public int size() {
        return queue.size();
    }
    
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
