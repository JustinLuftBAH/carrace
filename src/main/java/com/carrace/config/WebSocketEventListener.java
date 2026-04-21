package com.carrace.config;

import com.carrace.service.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    
    private final LobbyService lobbyService;
    
    // Track which user/race each session is associated with
    private final Map<String, UserRaceSession> sessionMap = new ConcurrentHashMap<>();
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.debug("WebSocket connection established");
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null) {
            UserRaceSession session = sessionMap.remove(sessionId);
            if (session != null) {
                log.info("WebSocket disconnected - removing user {} from race {}", 
                    session.getUserId(), session.getRaceId());
                lobbyService.leaveRace(session.getRaceId(), session.getUserId());
            }
        }
    }
    
    public void registerSession(String sessionId, Long userId, Long raceId) {
        sessionMap.put(sessionId, new UserRaceSession(userId, raceId));
        log.debug("Registered session {} for user {} in race {}", sessionId, userId, raceId);
    }
    
    private static class UserRaceSession {
        private final Long userId;
        private final Long raceId;
        
        public UserRaceSession(Long userId, Long raceId) {
            this.userId = userId;
            this.raceId = raceId;
        }
        
        public Long getUserId() { return userId; }
        public Long getRaceId() { return raceId; }
    }
}
