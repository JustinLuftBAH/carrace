package com.carrace.dto;

import com.carrace.model.RaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceDetailsResponse {
    private Long raceId;
    private RaceStatus status;
    private List<CarInfo> cars;
    private List<LeaderboardEntry> leaderboard;
    private Long winnerCarId;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarInfo {
        private Long id;
        private String name;
        private String color;
        private Integer position;
        private Double speed;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaderboardEntry {
        private Long carId;
        private String carName;
        private Integer position;
        private Double speed;
        private Integer rank;
    }
}
