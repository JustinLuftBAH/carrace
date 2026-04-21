package com.carrace.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceStartedEvent {
    private Long raceId;
    private Long timestamp;
}
