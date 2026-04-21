package com.carrace.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceFinishedEvent {
    private Long raceId;
    private Long winnerCarId;
    private Long timestamp;
}
