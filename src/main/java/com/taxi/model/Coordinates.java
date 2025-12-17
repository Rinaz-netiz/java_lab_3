package com.taxi.model;

import java.util.Random;

public record Coordinates(int x, int y) {
    private static final Random RANDOM = new Random();
    
    public double distanceTo(Coordinates other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public static Coordinates random(int citySize) {
        return new Coordinates(RANDOM.nextInt(citySize), RANDOM.nextInt(citySize));
    }
    
    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }
}
