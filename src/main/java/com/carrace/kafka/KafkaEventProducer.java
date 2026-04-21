package com.carrace.kafka;

import com.carrace.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.race-events:race-events}")
    private String raceEventsTopic;
    
    @Value("${kafka.topic.bet-events:bet-events}")
    private String betEventsTopic;
    
    @Value("${kafka.topic.payout-events:payout-events}")
    private String payoutEventsTopic;
    
    @Value("${kafka.topic.leaderboard-updates:leaderboard-updates}")
    private String leaderboardUpdatesTopic;
    
    public void publishRaceStarted(RaceStartedEvent event) {
        String key = String.valueOf(event.getRaceId());
        log.info("Publishing RaceStartedEvent for race {}", event.getRaceId());
        kafkaTemplate.send(raceEventsTopic, key, event);
    }
    
    public void publishCarPositionUpdate(CarPositionUpdatedEvent event) {
        String key = String.valueOf(event.getRaceId());
        log.debug("Publishing CarPositionUpdatedEvent for race {} car {}", 
            event.getRaceId(), event.getCarId());
        kafkaTemplate.send(raceEventsTopic, key, event);
    }
    
    public void publishRaceFinished(RaceFinishedEvent event) {
        String key = String.valueOf(event.getRaceId());
        log.info("Publishing RaceFinishedEvent for race {}, winner: {}", 
            event.getRaceId(), event.getWinnerCarId());
        kafkaTemplate.send(raceEventsTopic, key, event);
    }
    
    public void publishBetPlaced(BetPlacedEvent event) {
        String key = String.valueOf(event.getRaceId());
        log.info("Publishing BetPlacedEvent for user {} on race {}", 
            event.getUserId(), event.getRaceId());
        kafkaTemplate.send(betEventsTopic, key, event);
    }
    
    public void publishPayoutCalculated(PayoutCalculatedEvent event) {
        String key = String.valueOf(event.getRaceId());
        log.info("Publishing PayoutCalculatedEvent for user {} on race {}, amount: {}", 
            event.getUserId(), event.getRaceId(), event.getAmount());
        kafkaTemplate.send(payoutEventsTopic, key, event);
    }
    
    public void publishLeaderboardUpdate(LeaderboardUpdateEvent event) {
        String key = String.valueOf(event.getRaceId());
        log.debug("Publishing LeaderboardUpdateEvent for race {}", event.getRaceId());
        kafkaTemplate.send(leaderboardUpdatesTopic, key, event);
    }
}
