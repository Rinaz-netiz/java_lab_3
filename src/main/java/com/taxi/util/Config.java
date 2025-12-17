package com.taxi.util;

public final class Config {
    private Config() {}
    
    public static final int TAXI_COUNT = 5;
    public static final int CITY_SIZE = 100;
    public static final long ORDER_GENERATION_DELAY_MIN = 1000;
    public static final long ORDER_GENERATION_DELAY_MAX = 3000;
    public static final long SIMULATION_DURATION = 30000;
    public static final int SPEED_FACTOR = 50;
    public static final int ORDER_QUEUE_CAPACITY = 100;
}
