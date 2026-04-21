package com.carrace.entity;

import com.carrace.model.BetStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "race_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "race_id", nullable = false)
    private Long raceId;
    
    @Column(nullable = false)
    private Long carId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private Boolean isFreeBet = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetStatus status = BetStatus.PENDING;
    
    @Column(nullable = false)
    private LocalDateTime placedAt = LocalDateTime.now();
    
    @Column
    private BigDecimal payout;
    
    @Version
    private Long version;
}
