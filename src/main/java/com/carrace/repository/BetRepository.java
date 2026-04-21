package com.carrace.repository;

import com.carrace.entity.Bet;
import com.carrace.model.BetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByRaceId(Long raceId);
    List<Bet> findByRaceIdAndStatus(Long raceId, BetStatus status);
    Optional<Bet> findByUserIdAndRaceId(Long userId, Long raceId);
    List<Bet> findByUserId(Long userId);
}
