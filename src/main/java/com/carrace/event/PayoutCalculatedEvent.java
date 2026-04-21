package com.carrace.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutCalculatedEvent {
    private Long userId;
    private Long raceId;
    private Long betId;
    private BigDecimal amount;
    private Long timestamp;
}
