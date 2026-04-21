package com.carrace.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarPositionUpdatedEvent {
    private Long raceId;
    private Long carId;
    private Integer position;
    private Double speed;
    private Long timestamp;
}
