package com.carrace.repository;

import com.carrace.entity.Race;
import com.carrace.model.RaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {
    List<Race> findByStatus(RaceStatus status);
}
