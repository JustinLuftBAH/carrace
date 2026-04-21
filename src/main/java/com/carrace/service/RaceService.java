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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RaceService {
    
    private final RaceRepository raceRepository;
    private final CarRepository carRepository;
    private final KafkaEventProducer eventProducer;
    
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
        
        // Create default cars for the race with random names and probabilities
        for (int i = 0; i < defaultCarsPerRace; i++) {
            String carName = CarNameGenerator.generate();
            String carColor = com.carrace.util.ColorGenerator.generateVibrantHex();
            // Generate random win probability between 1.5x and 5.0x
            Double winProbability = 1.5 + (Math.random() * 3.5);
            winProbability = Math.round(winProbability * 10.0) / 10.0; // Round to 1 decimal
            
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
