package com.carrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceParticipant {
    private Long userId;
    private String username;
    private boolean ready;
    private BetInfo bet;
    private String profilePicture;
    private String nameColor;
    private String customNametag;
    private Double balance;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BetInfo {
        private Long carId;
        private String carName;
        private Double amount;
    }
}
