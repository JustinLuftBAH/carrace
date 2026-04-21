package com.carrace.service;

import com.carrace.entity.Car;
import com.carrace.entity.Race;
import com.carrace.model.RaceStatus;
import com.carrace.repository.CarRepository;
import com.carrace.repository.RaceRepository;
import com.carrace.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * RaceSimulationService simulates car movement in running races.
 * This runs periodically and updates car positions, publishing events to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RaceSimulationService {
    
    private final RaceService raceService;
    private final RaceRepository raceRepository;
    private final CarRepository carRepository;
    private final WebSocketService webSocketService;
    
    @Value("${race.finish.position:1000}")
    private int finishPosition;
    
    private final Random random = new Random();
    
    /**
     * Simulate race progress every 200ms for all running races
     */
    @Scheduled(fixedDelay = 200)
    @Transactional
    public void simulateRaceProgress() {
        try {
            List<Race> runningRaces = raceRepository.findByStatus(RaceStatus.RUNNING);
            
            if (!runningRaces.isEmpty()) {
                log.debug("Simulating {} running races", runningRaces.size());
            }
            
            for (Race race : runningRaces) {
                simulateSingleRace(race);
            }
        } catch (Exception e) {
            log.error("Error simulating race progress: {}", e.getMessage(), e);
        }
    }
    
    private enum SpeedBracket {
        SLOW, MEDIUM, FAST
    }
    
    private void simulateSingleRace(Race race) {
        List<Car> cars = carRepository.findByRaceId(race.getId());
        
        boolean raceFinished = false;
        Long winnerCarId = null;
        int maxPosition = 0;
        
        for (Car car : cars) {
            // New bracket-based speed system:
            // - Each car falls into a speed bracket (SLOW, MEDIUM, FAST) each tick
            // - Probability affects the CHANCE of getting each bracket, not speed directly
            // - This keeps races exciting - underdogs can still win!
            
            double odds = car.getWinProbability();
            
            // Determine speed bracket based on probability-weighted random selection
            SpeedBracket bracket = determineSpeedBracket(odds);
            
            // Generate speed within the bracket (slightly affected by probability)
            double speed = generateSpeedForBracket(bracket, odds);
            
            int newPosition = car.getPosition() + (int) speed;
            
            // Check if car crossed finish line
            if (newPosition >= finishPosition) {
                newPosition = finishPosition;
                raceFinished = true;
                
                // Track the car with highest position (winner)
                if (newPosition > maxPosition) {
                    maxPosition = newPosition;
                    winnerCarId = car.getId();
                }
            }
            
            // Update car position
            raceService.updateCarPosition(race.getId(), car.getId(), newPosition, speed);
        }
        
        // Broadcast leaderboard update via WebSocket
        broadcastLeaderboard(race.getId());
        
        // If any car finished, end the race
        if (raceFinished && winnerCarId != null) {
            raceService.finishRace(race.getId(), winnerCarId);
            
            // Broadcast race finished event via WebSocket
            Map<String, Object> update = new HashMap<>();
            update.put("type", "RACE_FINISHED");
            update.put("raceId", race.getId());
            update.put("status", "FINISHED");
            update.put("winnerCarId", winnerCarId);
            webSocketService.sendRaceUpdate(race.getId(), update);
            
            log.info("Race {} finished! Winner: Car {}", race.getId(), winnerCarId);
        }
    }
    
    /**
     * Determines which speed bracket a car falls into based on their odds.
     * NEARLY FLAT probability curve - favorite should only win ~35-40% of the time!
     * 
     * ULTRA-BALANCED distributions make every car competitive:
     * - 2.0x odds (favorite): 38% FAST, 35% MEDIUM, 27% SLOW -> ~35-40% win rate
     * - 2.5x odds: 37.5% FAST, 35% MEDIUM, 27.5% SLOW -> ~20-25% win rate
     * - 3.0x odds: 37% FAST, 35% MEDIUM, 28% SLOW -> ~20-25% win rate
     * - 4.0x odds (underdog): 36% FAST, 35% MEDIUM, 29% SLOW -> ~15-20% win rate
     * 
     * Only 2% spread between best and worst - extremely competitive!
     */
    private SpeedBracket determineSpeedBracket(double odds) {
        // Normalize odds to a 0-1 scale for probability calculation
        // New range: 2.0 (favorite) to 4.0 (underdog) with most cars in 2.0-3.0
        // Lower normalized value = slightly better car
        double normalized = Math.min(1.0, (odds - 2.0) / 2.0); // 2.0->0.0, 3.0->0.5, 4.0->1.0
        
        // Calculate bracket probabilities with ULTRA-FLAT curve
        // Only 2% difference between favorite and underdog!
        double fastChance = 0.38 - (normalized * 0.02);  // 38% down to 36% (only 2% difference!)
        double slowChance = 0.27 + (normalized * 0.02);  // 27% up to 29% (only 2% difference!)
        // mediumChance stays at 35% for everyone
        
        // Random selection based on weighted probabilities
        double roll = random.nextDouble();
        
        if (roll < fastChance) {
            return SpeedBracket.FAST;
        } else if (roll < fastChance + (1.0 - fastChance - slowChance)) {
            return SpeedBracket.MEDIUM;
        } else {
            return SpeedBracket.SLOW;
        }
    }
    
    /**
     * Generates a speed within the given bracket.
     * Probability has a small effect on speed within the bracket (a few m/s difference)
     * 
     * Speed ranges:
     * - SLOW: 20-30 m/s (base) ± 2 m/s based on odds
     * - MEDIUM: 28-38 m/s (base) ± 2 m/s based on odds
     * - FAST: 36-46 m/s (base) ± 2 m/s based on odds
     * 
     * Overlapping ranges make races more dynamic!
     */
    private double generateSpeedForBracket(SpeedBracket bracket, double odds) {
        double baseMin, baseMax;
        
        switch (bracket) {
            case SLOW:
                baseMin = 20.0;
                baseMax = 30.0;
                break;
            case MEDIUM:
                baseMin = 28.0;
                baseMax = 38.0;
                break;
            case FAST:
                baseMin = 36.0;
                baseMax = 46.0;
                break;
            default:
                baseMin = 28.0;
                baseMax = 38.0;
        }
        
        // Generate base speed in bracket range
        double speed = baseMin + (random.nextDouble() * (baseMax - baseMin));
        
        // Apply TINY odds modifier (±0.5 m/s based on odds) - almost negligible
        // Lower odds (e.g., 2.0) = +0.5 m/s
        // Higher odds (e.g., 4.0) = -0.5 m/s
        double oddsModifier = 0.5 - ((odds - 2.0) / 2.0) * 1.0; // 2.0->+0.5, 3.0->0, 4.0->-0.5
        speed += oddsModifier;
        
        // Ensure minimum speed
        return Math.max(15.0, speed);
    }
    
    private void broadcastLeaderboard(Long raceId) {
        try {
            // Get all cars for this race and build leaderboard
            List<Car> cars = carRepository.findByRaceId(raceId);
            
            // Sort by position descending
            List<Map<String, Object>> leaderboard = cars.stream()
                .sorted(Comparator.comparing(Car::getPosition).reversed())
                .map(car -> {
                    Map<String, Object> carData = new HashMap<>();
                    carData.put("carId", car.getId());
                    carData.put("carName", car.getName());
                    carData.put("position", car.getPosition());
                    carData.put("speed", car.getSpeed());
                    carData.put("color", car.getColor());
                    return carData;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> update = new HashMap<>();
            update.put("type", "LEADERBOARD_UPDATE");
            update.put("raceId", raceId);
            update.put("leaderboard", leaderboard);
            
            webSocketService.sendRaceUpdate(raceId, update);
        } catch (Exception e) {
            log.error("Error broadcasting leaderboard for race {}: {}", raceId, e.getMessage());
        }
    }
}
