package com.carrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyUpdateMessage {
    private Long raceId;
    private List<RaceParticipant> participants;
    private Integer readyCountdown;
    private Boolean raceStarting;
}
