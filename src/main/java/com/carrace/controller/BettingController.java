package com.carrace.controller;

import com.carrace.dto.PlaceBetRequest;
import com.carrace.entity.Bet;
import com.carrace.service.BettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bet")
@RequiredArgsConstructor
@Slf4j
public class BettingController {
    
    private final BettingService bettingService;
    
    @PostMapping("/place")
    public ResponseEntity<Map<String, Object>> placeBet(@Valid @RequestBody PlaceBetRequest request) {
        Bet bet = bettingService.placeBet(
            request.getUserId(),
            request.getRaceId(),
            request.getCarId(),
            request.getAmount()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("betId", bet.getId());
        response.put("status", bet.getStatus().name());
        response.put("message", "Bet placed successfully");
        
        return ResponseEntity.ok(response);
    }
}
