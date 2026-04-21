package com.carrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionDTO {
    private Long userId;
    private String username;
    private String emoji;
    private Long timestamp;
    
    public ReactionDTO(Long userId, String username, String emoji) {
        this.userId = userId;
        this.username = username;
        this.emoji = emoji;
        this.timestamp = System.currentTimeMillis();
    }
}
