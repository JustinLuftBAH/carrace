package com.carrace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;
    
    @Column(nullable = false)
    private Integer position = 0;
    
    @Column(nullable = false)
    private Double speed = 0.0;
    
    @Column(nullable = false)
    private String name; // e.g., "Car 1", "Red Racer", etc.
    
    @Column(nullable = false)
    private String color; // For UI display
    
    @Column(nullable = false)
    private Double winProbability = 2.0; // Odds multiplier (e.g., 2.0x, 3.5x)
    
    @Version
    private Long version; // For optimistic locking
    
    public Car(String name, String color) {
        this.name = name;
        this.color = color;
        this.position = 0;
        this.speed = 0.0;
        this.winProbability = 2.0;
    }
    
    public Car(String name, String color, Double winProbability) {
        this.name = name;
        this.color = color;
        this.position = 0;
        this.speed = 0.0;
        this.winProbability = winProbability;
    }
}
