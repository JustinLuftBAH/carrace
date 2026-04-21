package com.carrace.websocket;

import com.carrace.event.LeaderboardUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendRaceUpdate(Long raceId, Object message) {
        String destination = "/topic/race/" + raceId;
        log.info("Sending WebSocket message to {} : {}", destination, message);
        messagingTemplate.convertAndSend(destination, message);
    }
    
    public void sendLobbyUpdate(Long raceId, Object message) {
        String destination = "/topic/lobby/" + raceId;
        log.debug("Sending lobby update to {} : {}", destination, message);
        messagingTemplate.convertAndSend(destination, message);
    }
    
    public void sendLeaderboardUpdate(LeaderboardUpdateEvent event) {
        String destination = "/topic/race/" + event.getRaceId();
        log.debug("Sending leaderboard update to {}", destination);
        messagingTemplate.convertAndSend(destination, event);
    }
}
