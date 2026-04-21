package com.carrace.controller;

import com.carrace.dto.ReactionDTO;
import com.carrace.entity.User;
import com.carrace.repository.UserRepository;
import com.carrace.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReactionController {
    
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendReaction(
            @RequestParam Long userId,
            @RequestParam Long raceId,
            @RequestParam String emoji) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Create reaction DTO
        ReactionDTO reaction = new ReactionDTO(userId, user.getUsername(), emoji);
        
        // Broadcast to lobby
        webSocketService.sendLobbyUpdate(raceId, Map.of(
            "type", "REACTION",
            "reaction", reaction
        ));
        
        return ResponseEntity.ok(Map.of("message", "Reaction sent"));
    }
}
