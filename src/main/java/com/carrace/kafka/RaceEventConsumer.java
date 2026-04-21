package com.carrace.kafka;

import com.carrace.entity.Car;
import com.carrace.event.CarPositionUpdatedEvent;
import com.carrace.event.LeaderboardUpdateEvent;
import com.carrace.event.RaceFinishedEvent;
import com.carrace.event.RaceStartedEvent;
import com.carrace.repository.CarRepository;
import com.carrace.service.PayoutService;
import com.carrace.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RaceEventConsumer {
    
    private final PayoutService payoutService;
    private final CarRepository carRepository;
    private final WebSocketService webSocketService;
    
    @KafkaListener(
        topics = "${kafka.topic.race-events:race-events}",
        groupId = "${kafka.consumer.group.race-processor:race-processor-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeRaceEvent(ConsumerRecord<String, Object> record) {
        Object event = record.value();
        log.info("Received race event: {} - {}", event.getClass().getSimpleName(), event);
        
        if (event instanceof RaceStartedEvent) {
            handleRaceStarted((RaceStartedEvent) event);
        } else if (event instanceof CarPositionUpdatedEvent) {
            handleCarPositionUpdate((CarPositionUpdatedEvent) event);
        } else if (event instanceof RaceFinishedEvent) {
            handleRaceFinished((RaceFinishedEvent) event);
        }
    }
    
    private void handleRaceStarted(RaceStartedEvent event) {
        log.info("Processing race started event for race {}", event.getRaceId());
        try {
            // Send race started notification via WebSocket
            Map<String, Object> update = Map.of(
                "type", "RACE_STARTED",
                "raceId", event.getRaceId(),
                "status", "RUNNING"
            );
            webSocketService.sendRaceUpdate(event.getRaceId(), update);
            
            // Also send initial leaderboard
            broadcastLeaderboard(event.getRaceId());
        } catch (Exception e) {
            log.error("Error processing race started event: {}", e.getMessage(), e);
        }
    }
    
    private void handleCarPositionUpdate(CarPositionUpdatedEvent event) {
        log.debug("Processing car position update for race {} car {}", 
            event.getRaceId(), event.getCarId());
        try {
            // Broadcast updated leaderboard to all connected clients
            broadcastLeaderboard(event.getRaceId());
        } catch (Exception e) {
            log.error("Error processing car position update: {}", e.getMessage(), e);
        }
    }
    
    private void handleRaceFinished(RaceFinishedEvent event) {
        log.info("Processing race finished event for race {}", event.getRaceId());
        try {
            // Send race finished notification via WebSocket
            Map<String, Object> update = Map.of(
                "type", "RACE_FINISHED",
                "raceId", event.getRaceId(),
                "status", "FINISHED",
                "winnerCarId", event.getWinnerCarId()
            );
            webSocketService.sendRaceUpdate(event.getRaceId(), update);
            
            // Calculate payouts
            payoutService.calculatePayouts(event.getRaceId(), event.getWinnerCarId());
        } catch (Exception e) {
            log.error("Error processing race finished event: {}", e.getMessage(), e);
        }
    }
    
    private void broadcastLeaderboard(Long raceId) {
        // Get all cars for this race
        List<Car> cars = carRepository.findByRaceId(raceId);
        
        // Build leaderboard sorted by position (descending)
        List<LeaderboardUpdateEvent.LeaderboardEntry> leaderboard = cars.stream()
            .sorted(Comparator.comparing(Car::getPosition).reversed())
            .map(car -> new LeaderboardUpdateEvent.LeaderboardEntry(
                car.getId(),
                car.getName(),
                car.getPosition(),
                car.getSpeed()
            ))
            .collect(Collectors.toList());
        
        // Create and send leaderboard update
        Map<String, Object> update = Map.of(
            "type", "LEADERBOARD_UPDATE",
            "raceId", raceId,
            "leaderboard", leaderboard
        );
        
        webSocketService.sendRaceUpdate(raceId, update);
    }
}
