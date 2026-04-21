package com.carrace.streams;

import com.carrace.event.CarPositionUpdatedEvent;
import com.carrace.event.LeaderboardUpdateEvent;
import com.carrace.websocket.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Kafka Streams processor for building real-time race leaderboards.
 * 
 * This processor:
 * 1. Consumes CarPositionUpdatedEvent from race-events topic
 * 2. Groups events by raceId
 * 3. Maintains a KTable of car positions per race
 * 4. Sorts cars by position to create a leaderboard
 * 5. Publishes leaderboard updates via WebSocket
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class LeaderboardStreamsProcessor {
    
    private final WebSocketService webSocketService;
    
    @Value("${kafka.topic.race-events:race-events}")
    private String raceEventsTopic;
    
    @Value("${kafka.topic.leaderboard-updates:leaderboard-updates}")
    private String leaderboardUpdatesTopic;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Bean
    public KStream<String, Object> leaderboardStream(StreamsBuilder builder) {
        // Configure JSON Serde for event deserialization
        JsonSerde<Object> eventSerde = new JsonSerde<>(Object.class, objectMapper);
        eventSerde.ignoreTypeHeaders();
        
        // Read from race-events topic
        KStream<String, Object> raceEvents = builder.stream(
            raceEventsTopic,
            Consumed.with(Serdes.String(), eventSerde)
        );
        
        // Filter for only CarPositionUpdatedEvent
        KStream<String, CarPositionUpdatedEvent> positionUpdates = raceEvents
            .filter((key, value) -> value instanceof LinkedHashMap)
            .mapValues(value -> {
                try {
                    return objectMapper.convertValue(value, CarPositionUpdatedEvent.class);
                } catch (Exception e) {
                    log.warn("Failed to convert event to CarPositionUpdatedEvent: {}", e.getMessage());
                    return null;
                }
            })
            .filter((key, value) -> value != null);
        
        // Group by raceId
        KGroupedStream<String, CarPositionUpdatedEvent> groupedByRace = positionUpdates
            .selectKey((key, value) -> String.valueOf(value.getRaceId()))
            .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(CarPositionUpdatedEvent.class, objectMapper)));
        
        // Aggregate car positions per race
        KTable<String, Map<Long, CarPositionUpdatedEvent>> racePositions = groupedByRace
            .aggregate(
                HashMap::new,
                (raceId, update, positions) -> {
                    positions.put(update.getCarId(), update);
                    return positions;
                },
                Materialized.with(Serdes.String(), new JsonSerde<>(Map.class, objectMapper))
            );
        
        // Convert to leaderboard and publish
        racePositions.toStream()
            .foreach((raceId, positions) -> {
                if (positions != null && !positions.isEmpty()) {
                    publishLeaderboard(Long.parseLong(raceId), positions);
                }
            });
        
        return raceEvents;
    }
    
    private void publishLeaderboard(Long raceId, Map<Long, CarPositionUpdatedEvent> positions) {
        try {
            // Sort cars by position (descending)
            List<LeaderboardUpdateEvent.LeaderboardEntry> leaderboard = positions.values().stream()
                .sorted(Comparator.comparing(CarPositionUpdatedEvent::getPosition).reversed())
                .map(update -> new LeaderboardUpdateEvent.LeaderboardEntry(
                    update.getCarId(),
                    "Car " + update.getCarId(),
                    update.getPosition(),
                    update.getSpeed()
                ))
                .collect(Collectors.toList());
            
            LeaderboardUpdateEvent event = new LeaderboardUpdateEvent(
                raceId,
                leaderboard,
                System.currentTimeMillis()
            );
            
            // Send to WebSocket clients
            webSocketService.sendLeaderboardUpdate(event);
            
            log.debug("Published leaderboard for race {} with {} cars", raceId, leaderboard.size());
        } catch (Exception e) {
            log.error("Error publishing leaderboard for race {}: {}", raceId, e.getMessage(), e);
        }
    }
}
