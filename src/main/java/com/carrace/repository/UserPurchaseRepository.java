package com.carrace.repository;

import com.carrace.entity.UserPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPurchaseRepository extends JpaRepository<UserPurchase, Long> {
    List<UserPurchase> findByUserId(Long userId);
    Optional<UserPurchase> findByUserIdAndShopItemId(Long userId, Long shopItemId);
    boolean existsByUserIdAndShopItemId(Long userId, Long shopItemId);
}
