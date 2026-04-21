package com.carrace.service;

import com.carrace.entity.Car;
import com.carrace.entity.Race;
import com.carrace.event.CarPositionUpdatedEvent;
import com.carrace.event.RaceFinishedEvent;
import com.carrace.event.RaceStartedEvent;
import com.carrace.exception.InvalidStateException;
import com.carrace.exception.RaceNotFoundException;
import com.carrace.kafka.KafkaEventProducer;
import com.carrace.model.RaceStatus;
import com.carrace.repository.CarRepository;
import com.carrace.repository.RaceRepository;
import com.carrace.util.CarNameGenerator;
import com.carrace.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RaceService {
    
    private final RaceRepository raceRepository;
    private final CarRepository carRepository;
    private final KafkaEventProducer eventProducer;
    private final WebSocketService webSocketService;
    
    @Value("${race.default.cars:4}")
    private int defaultCarsPerRace;
    
    private static final List<String> CAR_COLORS = Arrays.asList(
        "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF"
    );
    
    @Transactional
    public Race createRace() {
        Race race = new Race();
        race.setStatus(RaceStatus.CREATED);
        race.setCreatedAt(LocalDateTime.now());
        
        race = raceRepository.save(race);
        
        // Create cars with unique win probabilities
        // Lower odds = slightly better chance, but MUCH closer together for competitive racing!
        // Most cars have similar odds (2.0x-3.0x), with one potential underdog at ~4.0x
        java.util.List<Double> allOdds = new java.util.ArrayList<>();
        
        // Generate competitive odds where everyone has a real chance
        // Most cars clustered in 2.0x-3.0x range (very competitive!)
        if (defaultCarsPerRace <= 1) {
            allOdds.add(2.5);
        } else {
            // Generate most cars in tight 2.0x-3.0x range
            double competitiveRange = 1.0; // 3.0 - 2.0 = 1.0
            double step = competitiveRange / Math.max(1, defaultCarsPerRace - 2); // Reserve one slot for underdog
            
            for (int i = 0; i < defaultCarsPerRace - 1; i++) {
                Double odds = 2.0 + (step * i);
                odds = Math.round(odds * 10.0) / 10.0;
                allOdds.add(odds);
            }
            
            // Add one underdog at 4.0x (still has ~35% chance at FAST bracket!)
            allOdds.add(4.0);
        }
        
        // Shuffle odds to randomly assign to cars
        java.util.Collections.shuffle(allOdds);
        
        // Create cars with shuffled unique odds
        for (int i = 0; i < defaultCarsPerRace; i++) {
            String carName = CarNameGenerator.generate();
            String carColor = com.carrace.util.ColorGenerator.generateVibrantHex();
            Double winProbability = allOdds.get(i);
            
            Car car = new Car(carName, carColor, winProbability);
            car.setRace(race);
            carRepository.save(car);
        }
        
        log.info("Created race {} with {} cars", race.getId(), defaultCarsPerRace);
        return race;
    }
    
    @Transactional
    public void startRace(Long raceId) {
        Race race = raceRepository.findById(raceId)
            .orElseThrow(() -> new RaceNotFoundException(raceId));
        
        if (race.getStatus() != RaceStatus.CREATED) {
            throw new InvalidStateException(
                "Cannot start race in status: " + race.getStatus());
        }
        
        race.setStatus(RaceStatus.RUNNING);
        race.setStartedAt(LocalDateTime.now());
        raceRepository.save(race);
        
        // Publish event to Kafka
        RaceStartedEvent event = new RaceStartedEvent(
            raceId, 
            System.currentTimeMillis()
        );
        eventProducer.publishRaceStarted(event);
        
        // Broadcast race started via WebSocket
        Map<String, Object> update = new HashMap<>();
        update.put("type", "RACE_STARTED");
        update.put("raceId", raceId);
        update.put("status", "RUNNING");
        webSocketService.sendRaceUpdate(raceId, update);
        
        log.info("Started race {}", raceId);
    }
    
    @Transactional
    public void updateCarPosition(Long raceId, Long carId, Integer position, Double speed) {
        Race race = raceRepository.findById(raceId)
            .orElseThrow(() -> new RaceNotFoundException(raceId));
        
        if (race.getStatus() != RaceStatus.RUNNING) {
            throw new InvalidStateException(
                "Cannot update car position in race status: " + race.getStatus());
        }
        
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new RuntimeException("Car not found: " + carId));
        
        if (!car.getRace().getId().equals(raceId)) {
            throw new InvalidStateException("Car does not belong to this race");
        }
        
        car.setPosition(position);
        car.setSpeed(speed);
        carRepository.save(car);
        
        // Publish position update event
        CarPositionUpdatedEvent event = new CarPositionUpdatedEvent(
            raceId, carId, position, speed, System.currentTimeMillis()
        );
        eventProducer.publishCarPositionUpdate(event);
        
        log.debug("Updated car {} position to {} in race {}", carId, position, raceId);
    }
    
    @Transactional
    public void finishRace(Long raceId, Long winnerCarId) {
        Race race = raceRepository.findById(raceId)
            .orElseThrow(() -> new RaceNotFoundException(raceId));
        
        if (race.getStatus() != RaceStatus.RUNNING) {
            throw new InvalidStateException(
                "Cannot finish race in status: " + race.getStatus());
        }
        
        race.setStatus(RaceStatus.FINISHED);
        race.setFinishedAt(LocalDateTime.now());
        race.setWinnerCarId(winnerCarId);
        raceRepository.save(race);
        
        // Publish race finished event
        RaceFinishedEvent event = new RaceFinishedEvent(
            raceId, winnerCarId, System.currentTimeMillis()
        );
        eventProducer.publishRaceFinished(event);
        
        log.info("Finished race {}, winner: car {}", raceId, winnerCarId);
    }
    
    @Transactional(readOnly = true)
    public Race getRace(Long raceId) {
        return raceRepository.findById(raceId)
            .orElseThrow(() -> new RaceNotFoundException(raceId));
    }
    
    @Transactional(readOnly = true)
    public List<Car> getRaceCars(Long raceId) {
        return carRepository.findByRaceId(raceId);
    }
    
    @Transactional(readOnly = true)
    public List<Race> getActiveRaces() {
        return raceRepository.findByStatus(RaceStatus.CREATED);
    }
}
