package com.carrace.service;

import com.carrace.entity.Bet;
import com.carrace.entity.Race;
import com.carrace.entity.User;
import com.carrace.event.BetPlacedEvent;
import com.carrace.exception.InsufficientBalanceException;
import com.carrace.exception.InvalidStateException;
import com.carrace.exception.RaceNotFoundException;
import com.carrace.exception.UserNotFoundException;
import com.carrace.kafka.KafkaEventProducer;
import com.carrace.model.BetStatus;
import com.carrace.model.RaceStatus;
import com.carrace.repository.BetRepository;
import com.carrace.repository.RaceRepository;
import com.carrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BettingService {
    
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final KafkaEventProducer eventProducer;
    
    @Transactional
    public Bet placeBet(Long userId, Long raceId, Long carId, BigDecimal amount) {
        // Validate user exists and has sufficient balance
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                "Insufficient balance. Available: " + user.getBalance() + ", Required: " + amount);
        }
        
        // Validate race exists and is in CREATED status
        Race race = raceRepository.findById(raceId)
            .orElseThrow(() -> new RaceNotFoundException(raceId));
        
        if (race.getStatus() != RaceStatus.CREATED) {
            throw new InvalidStateException(
                "Cannot place bet on race in status: " + race.getStatus());
        }
        
        // Check if user already has a bet on this race
        Optional<Bet> existingBet = betRepository.findByUserIdAndRaceId(userId, raceId);
        if (existingBet.isPresent()) {
            throw new InvalidStateException(
                "User already has a bet on this race");
        }
        
        // Deduct balance from user
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
        
        // Create bet
        Bet bet = new Bet();
        bet.setUser(user);
        bet.setRaceId(raceId);
        bet.setCarId(carId);
        bet.setAmount(amount);
        bet.setStatus(BetStatus.PENDING);
        bet.setPlacedAt(LocalDateTime.now());
        bet = betRepository.save(bet);
        
        // Publish bet placed event
        BetPlacedEvent event = new BetPlacedEvent(
            userId, raceId, carId, amount, System.currentTimeMillis()
        );
        eventProducer.publishBetPlaced(event);
        
        log.info("User {} placed bet {} on car {} in race {} for amount {}", 
            userId, bet.getId(), carId, raceId, amount);
        
        return bet;
    }
}
