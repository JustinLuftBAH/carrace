package com.carrace.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardUpdateEvent {
    private Long raceId;
    private List<LeaderboardEntry> leaderboard;
    private Long timestamp;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaderboardEntry {
        private Long carId;
        private String carName;
        private Integer position;
        private Double speed;
    }
}
