package com.carrace.entity;

import com.carrace.model.RaceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "races")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Race {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RaceStatus status = RaceStatus.CREATED;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime finishedAt;
    
    @Column
    private Long winnerCarId;
    
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars = new ArrayList<>();
    
    @Version
    private Long version; // For optimistic locking
    
    public void addCar(Car car) {
        cars.add(car);
        car.setRace(this);
    }
    
    public void removeCar(Car car) {
        cars.remove(car);
        car.setRace(null);
    }
}
