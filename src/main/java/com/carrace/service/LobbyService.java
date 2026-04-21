package com.carrace.service;

import com.carrace.dto.LobbyUpdateMessage;
import com.carrace.dto.RaceParticipant;
import com.carrace.entity.Bet;
import com.carrace.entity.Car;
import com.carrace.entity.Race;
import com.carrace.entity.User;
import com.carrace.model.BetStatus;
import com.carrace.repository.BetRepository;
import com.carrace.repository.CarRepository;
import com.carrace.repository.RaceRepository;
import com.carrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LobbyService {
    
    private final RaceRepository raceRepository;
    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final CarRepository carRepository;
    private final RaceService raceService;
    private final SimpMessagingTemplate messagingTemplate;
    
    // Track participants per race: raceId -> Set<userId>
    private final Map<Long, Set<Long>> raceParticipants = new ConcurrentHashMap<>();
    
    // Track ready status: raceId -> Set<userId>
    private final Map<Long, Set<Long>> readyPlayers = new ConcurrentHashMap<>();
    
    // Track when all players became ready: raceId -> timestamp
    private final Map<Long, Long> allReadyTimestamp = new ConcurrentHashMap<>();
    
    public void joinRace(Long raceId, Long userId) {
        raceParticipants.computeIfAbsent(raceId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        log.info("User {} joined race {}", userId, raceId);
        broadcastLobbyUpdate(raceId);
    }
    
    public void leaveRace(Long raceId, Long userId) {
        Set<Long> participants = raceParticipants.get(raceId);
        if (participants != null) {
            participants.remove(userId);
        }
        
        Set<Long> ready = readyPlayers.get(raceId);
        if (ready != null) {
            ready.remove(userId);
        }
        
        log.info("User {} left race {}", userId, raceId);
        broadcastLobbyUpdate(raceId);
    }
    
    public void setReady(Long raceId, Long userId, boolean ready) {
        Set<Long> readySet = readyPlayers.computeIfAbsent(raceId, k -> ConcurrentHashMap.newKeySet());
        
        if (ready) {
            readySet.add(userId);
            log.info("User {} is ready for race {}", userId, raceId);
        } else {
            readySet.remove(userId);
            allReadyTimestamp.remove(raceId); // Reset countdown
            log.info("User {} is no longer ready for race {}", userId, raceId);
        }
        
        broadcastLobbyUpdate(raceId);
        checkAllReady(raceId);
    }
    
    public List<RaceParticipant> getParticipants(Long raceId) {
        Set<Long> participants = raceParticipants.getOrDefault(raceId, Collections.emptySet());
        Set<Long> ready = readyPlayers.getOrDefault(raceId, Collections.emptySet());
        
        List<RaceParticipant> result = new ArrayList<>();
        
        for (Long userId : participants) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) continue;
            
            RaceParticipant participant = new RaceParticipant();
            participant.setUserId(userId);
            participant.setUsername(user.getUsername());
            participant.setReady(ready.contains(userId));
            participant.setProfilePicture(user.getProfilePicture());
            participant.setNameColor(user.getNameColor());
            participant.setCustomNametag(user.getCustomNametag());
            participant.setBalance(user.getBalance().doubleValue());
            
            // Get bet info
            Bet bet = betRepository.findByUserIdAndRaceId(userId, raceId).orElse(null);
            if (bet != null) {
                Car car = carRepository.findById(bet.getCarId()).orElse(null);
                if (car != null) {
                    RaceParticipant.BetInfo betInfo = new RaceParticipant.BetInfo(
                        car.getId(),
                        car.getName(),
                        bet.getAmount().doubleValue()
                    );
                    participant.setBet(betInfo);
                }
            }
            
            result.add(participant);
        }
        
        return result;
    }
    
    private void checkAllReady(Long raceId) {
        Set<Long> participants = raceParticipants.get(raceId);
        Set<Long> ready = readyPlayers.get(raceId);
        
        if (participants == null || participants.isEmpty() || ready == null) {
            return;
        }
        
        boolean allReady = participants.size() > 0 && participants.equals(ready);
        
        if (allReady) {
            if (!allReadyTimestamp.containsKey(raceId)) {
                allReadyTimestamp.put(raceId, System.currentTimeMillis());
                log.info("All players ready for race {}, starting countdown", raceId);
            }
        } else {
            allReadyTimestamp.remove(raceId);
        }
    }
    
    @Scheduled(fixedDelay = 1000) // Check every second
    public void checkReadyCountdowns() {
        long now = System.currentTimeMillis();
        
        for (Map.Entry<Long, Long> entry : allReadyTimestamp.entrySet()) {
            Long raceId = entry.getKey();
            Long readyTime = entry.getValue();
            
            long elapsed = (now - readyTime) / 1000; // seconds
            int countdown = (int) (5 - elapsed);
            
            if (countdown > 0) {
                // Broadcast countdown
                LobbyUpdateMessage message = new LobbyUpdateMessage();
                message.setRaceId(raceId);
                message.setParticipants(getParticipants(raceId));
                message.setReadyCountdown(countdown);
                message.setRaceStarting(false);
                
                messagingTemplate.convertAndSend("/topic/lobby/" + raceId, message);
            } else if (countdown == 0) {
                // Start race!
                try {
                    Race race = raceRepository.findById(raceId).orElse(null);
                    if (race != null && race.getStatus() == com.carrace.model.RaceStatus.CREATED) {
                        log.info("Starting race {} - all players were ready for 5 seconds", raceId);
                        
                        // Send race starting message
                        LobbyUpdateMessage message = new LobbyUpdateMessage();
                        message.setRaceId(raceId);
                        message.setParticipants(getParticipants(raceId));
                        message.setReadyCountdown(0);
                        message.setRaceStarting(true);
                        messagingTemplate.convertAndSend("/topic/lobby/" + raceId, message);
                        
                        // Start the race
                        raceService.startRace(raceId);
                        
                        // Clean up
                        allReadyTimestamp.remove(raceId);
                        readyPlayers.remove(raceId);
                    }
                } catch (Exception e) {
                    log.error("Failed to start race {}: {}", raceId, e.getMessage());
                    allReadyTimestamp.remove(raceId);
                }
            }
        }
    }
    
    private void broadcastLobbyUpdate(Long raceId) {
        LobbyUpdateMessage message = new LobbyUpdateMessage();
        message.setRaceId(raceId);
        message.setParticipants(getParticipants(raceId));
        
        Long readyTime = allReadyTimestamp.get(raceId);
        if (readyTime != null) {
            long elapsed = (System.currentTimeMillis() - readyTime) / 1000;
            int countdown = (int) (5 - elapsed);
            message.setReadyCountdown(Math.max(0, countdown));
        }
        
        messagingTemplate.convertAndSend("/topic/lobby/" + raceId, message);
    }
    
    public void cleanupRace(Long raceId) {
        raceParticipants.remove(raceId);
        readyPlayers.remove(raceId);
        allReadyTimestamp.remove(raceId);
    }
}
