package com.carrace.controller;

import com.carrace.entity.User;
import com.carrace.exception.UserNotFoundException;
import com.carrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        BigDecimal initialBalance = new BigDecimal(request.getOrDefault("balance", "1000").toString());
        
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        User user = new User(username, initialBalance);
        user = userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("balance", user.getBalance());
        response.put("profilePicture", user.getProfilePicture());
        response.put("nameColor", user.getNameColor());
        response.put("customNametag", user.getCustomNametag());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("balance", user.getBalance());
        response.put("profilePicture", user.getProfilePicture());
        response.put("nameColor", user.getNameColor());
        response.put("customNametag", user.getCustomNametag());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/login/{username}")
    public ResponseEntity<Map<String, Object>> login(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("balance", user.getBalance());
        response.put("profilePicture", user.getProfilePicture());
        response.put("nameColor", user.getNameColor());
        response.put("customNametag", user.getCustomNametag());
        
        return ResponseEntity.ok(response);
    }
}
