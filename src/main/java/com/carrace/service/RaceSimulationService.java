package com.carrace.service;

import com.carrace.entity.Car;
import com.carrace.entity.Race;
import com.carrace.model.RaceStatus;
import com.carrace.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * RaceSimulationService simulates car movement in running races.
 * This runs periodically and updates car positions, publishing events to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RaceSimulationService {
    
    private final RaceService raceService;
    private final CarRepository carRepository;
    
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
            List<Race> runningRaces = carRepository.findAll().stream()
                .map(Car::getRace)
                .filter(race -> race.getStatus() == RaceStatus.RUNNING)
                .distinct()
                .toList();
            
            for (Race race : runningRaces) {
                simulateSingleRace(race);
            }
        } catch (Exception e) {
            log.error("Error simulating race progress: {}", e.getMessage(), e);
        }
    }
    
    private void simulateSingleRace(Race race) {
        List<Car> cars = carRepository.findByRaceId(race.getId());
        
        boolean raceFinished = false;
        Long winnerCarId = null;
        int maxPosition = 0;
        
        for (Car car : cars) {
            // Generate random speed between 5 and 15 units per update
            double speed = 5 + (random.nextDouble() * 10);
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
        
        // If any car finished, end the race
        if (raceFinished && winnerCarId != null) {
            raceService.finishRace(race.getId(), winnerCarId);
            log.info("Race {} finished! Winner: Car {}", race.getId(), winnerCarId);
        }
    }
}
