package com.carrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRaceResponse {
    private Long raceId;
    private String status;
    private String message;
}
