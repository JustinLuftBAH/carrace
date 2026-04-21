package com.carrace.controller;

import com.carrace.dto.CreateRaceResponse;
import com.carrace.dto.RaceDetailsResponse;
import com.carrace.dto.RaceParticipant;
import com.carrace.entity.Car;
import com.carrace.entity.Race;
import com.carrace.service.LobbyService;
import com.carrace.service.RaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/race")
@RequiredArgsConstructor
@Slf4j
public class RaceController {
    
    private final RaceService raceService;
    private final LobbyService lobbyService;
    
    @PostMapping("/create")
    public ResponseEntity<CreateRaceResponse> createRace() {
        // Check for existing active race first
        List<Race> activeRaces = raceService.getActiveRaces();
        Race race;
        
        if (!activeRaces.isEmpty()) {
            // Join existing active race
            race = activeRaces.get(0);
            log.info("Returning existing active race: {}", race.getId());
        } else {
            // Create new race
            race = raceService.createRace();
            log.info("Created new race: {}", race.getId());
        }
        
        CreateRaceResponse response = new CreateRaceResponse(
            race.getId(),
            race.getStatus().name(),
            activeRaces.isEmpty() ? "Race created successfully" : "Joined existing race"
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startRace(@PathVariable Long id) {
        raceService.startRace(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinRace(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        raceService.getRace(id); // Validate race exists
        lobbyService.joinRace(id, userId);
        log.info("User {} joined race {}", userId, id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveRace(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        lobbyService.leaveRace(id, userId);
        log.info("User {} left race {}", userId, id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/ready")
    public ResponseEntity<Void> setReady(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        lobbyService.setReady(id, userId, true);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/unready")
    public ResponseEntity<Void> setUnready(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        lobbyService.setReady(id, userId, false);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<RaceParticipant>> getParticipants(@PathVariable Long id) {
        List<RaceParticipant> participants = lobbyService.getParticipants(id);
        return ResponseEntity.ok(participants);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RaceDetailsResponse> getRaceDetails(@PathVariable Long id) {
        Race race = raceService.getRace(id);
        List<Car> cars = raceService.getRaceCars(id);
        
        // Convert cars to response format
        List<RaceDetailsResponse.CarInfo> carInfos = cars.stream()
            .map(car -> new RaceDetailsResponse.CarInfo(
                car.getId(),
                car.getName(),
                car.getColor(),
                car.getPosition(),
                car.getSpeed(),
                car.getWinProbability()
            ))
            .collect(Collectors.toList());
        
        // Build leaderboard sorted by position descending
        AtomicInteger rank = new AtomicInteger(1);
        List<RaceDetailsResponse.LeaderboardEntry> leaderboard = cars.stream()
            .sorted(Comparator.comparing(Car::getPosition).reversed())
            .map(car -> new RaceDetailsResponse.LeaderboardEntry(
                car.getId(),
                car.getName(),
                car.getPosition(),
                car.getSpeed(),
                rank.getAndIncrement()
            ))
            .collect(Collectors.toList());
        
        RaceDetailsResponse response = new RaceDetailsResponse(
            race.getId(),
            race.getStatus(),
            carInfos,
            leaderboard,
            race.getWinnerCarId()
        );
        
        return ResponseEntity.ok(response);
    }
}
