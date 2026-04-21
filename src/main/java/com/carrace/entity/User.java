package com.carrace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Version
    private Long version; // For optimistic locking on balance updates
    
    // Customization fields
    @Column(name = "profile_picture")
    private String profilePicture = "🏎️"; // Default emoji
    
    @Column(name = "name_color")
    private String nameColor = "#FFFFFF"; // Default white
    
    @Column(name = "custom_nametag")
    private String customNametag; // Optional custom tag (e.g., "Pro Racer", "Speed Demon")
    
    public User(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
        this.profilePicture = "🏎️";
        this.nameColor = "#FFFFFF";
    }
}
