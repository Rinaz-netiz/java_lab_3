package com.taxi.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED = "\u001B[31m";
    
    private Logger() {}
    
    public static synchronized void info(String component, String message) {
        System.out.printf("%s[%s]%s %s[%-12s]%s %s%n", 
            CYAN, LocalTime.now().format(TIME_FORMAT), RESET, 
            GREEN, component, RESET, message);
    }
    
    public static synchronized void warn(String component, String message) {
        System.out.printf("%s[%s]%s %s[%-12s]%s %s%s%n", 
            CYAN, LocalTime.now().format(TIME_FORMAT), RESET, 
            YELLOW, component, RESET, YELLOW, message + RESET);
    }
    
    public static synchronized void error(String component, String message) {
        System.out.printf("%s[%s]%s %s[%-12s]%s %s%s%n", 
            CYAN, LocalTime.now().format(TIME_FORMAT), RESET, 
            RED, component, RESET, RED, message + RESET);
    }
    
    public static synchronized void event(String component, String message) {
        System.out.printf("%s[%s]%s %s[%-12s]%s %s%n", 
            CYAN, LocalTime.now().format(TIME_FORMAT), RESET, 
            BLUE, component, RESET, message);
    }
    
    public static synchronized void separator() {
        System.out.println("═".repeat(70));
    }
}
