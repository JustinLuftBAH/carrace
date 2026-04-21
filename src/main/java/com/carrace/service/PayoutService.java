package com.carrace.service;

import com.carrace.entity.Bet;
import com.carrace.entity.Car;
import com.carrace.entity.User;
import com.carrace.event.PayoutCalculatedEvent;
import com.carrace.kafka.KafkaEventProducer;
import com.carrace.model.BetStatus;
import com.carrace.repository.BetRepository;
import com.carrace.repository.CarRepository;
import com.carrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {
    
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final KafkaEventProducer eventProducer;
    
    @Transactional
    public void calculatePayouts(Long raceId, Long winnerCarId) {
        List<Bet> pendingBets = betRepository.findByRaceIdAndStatus(raceId, BetStatus.PENDING);
        
        // Get the winning car for fallback odds if needed
        Car winningCar = carRepository.findById(winnerCarId)
            .orElseThrow(() -> new RuntimeException("Winning car not found: " + winnerCarId));
        
        for (Bet bet : pendingBets) {
            if (bet.getCarId().equals(winnerCarId)) {
                // Winning bet - use stored odds from bet
                BigDecimal payout;
                if (bet.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                    // Free bet - fixed $250 payout
                    payout = new BigDecimal("250.00");
                } else {
                    // Regular bet - multiply by odds at time of betting
                    // Use stored odds, or fallback to car's current odds if not set
                    Double odds = bet.getOdds();
                    if (odds == null) {
                        odds = winningCar.getWinProbability();
                        if (odds == null) {
                            odds = 2.0;
                        }
                    }
                    payout = bet.getAmount().multiply(BigDecimal.valueOf(odds));
                }
                bet.setStatus(BetStatus.WON);
                bet.setPayout(payout);
                
                // Credit user account
                User user = bet.getUser();
                user.setBalance(user.getBalance().add(payout));
                userRepository.save(user);
                
                // Publish payout event
                PayoutCalculatedEvent event = new PayoutCalculatedEvent(
                    user.getId(), raceId, bet.getId(), payout, System.currentTimeMillis()
                );
                eventProducer.publishPayoutCalculated(event);
                
                log.info("Paid out {} to user {} for bet {} on race {}", 
                    payout, user.getId(), bet.getId(), raceId);
            } else {
                // Losing bet
                bet.setStatus(BetStatus.LOST);
                bet.setPayout(BigDecimal.ZERO);
                
                log.info("Bet {} on race {} lost", bet.getId(), raceId);
            }
            
            betRepository.save(bet);
        }
    }
}
