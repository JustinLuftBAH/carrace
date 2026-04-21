package com.carrace.model;

public enum RaceStatus {
    CREATED,   // Allow join and bet
    RUNNING,   // Lock bets, accept position updates
    FINISHED   // Stop updates, calculate payouts
}
