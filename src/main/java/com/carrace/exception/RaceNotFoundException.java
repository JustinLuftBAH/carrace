package com.carrace.exception;

public class RaceNotFoundException extends RuntimeException {
    public RaceNotFoundException(String message) {
        super(message);
    }
    
    public RaceNotFoundException(Long raceId) {
        super("Race not found with ID: " + raceId);
    }
}
