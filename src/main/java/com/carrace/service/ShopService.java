package com.carrace.service;

import com.carrace.dto.ShopItemDTO;
import com.carrace.entity.ShopItem;
import com.carrace.entity.User;
import com.carrace.entity.UserPurchase;
import com.carrace.repository.ShopItemRepository;
import com.carrace.repository.UserPurchaseRepository;
import com.carrace.repository.UserRepository;
import com.carrace.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    
    private final ShopItemRepository shopItemRepository;
    private final UserPurchaseRepository userPurchaseRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    
    public List<ShopItemDTO> getAllShopItems(Long userId) {
        List<ShopItem> allItems = shopItemRepository.findAll();
        Set<Long> ownedItemIds = userPurchaseRepository.findByUserId(userId)
            .stream()
            .map(purchase -> purchase.getShopItem().getId())
            .collect(Collectors.toSet());
        
        return allItems.stream()
            .map(item -> ShopItemDTO.from(item, ownedItemIds.contains(item.getId())))
            .collect(Collectors.toList());
    }
    
    public List<ShopItemDTO> getShopItemsByType(ShopItem.ItemType type, Long userId) {
        List<ShopItem> items = shopItemRepository.findByType(type);
        Set<Long> ownedItemIds = userPurchaseRepository.findByUserId(userId)
            .stream()
            .map(purchase -> purchase.getShopItem().getId())
            .collect(Collectors.toSet());
        
        return items.stream()
            .map(item -> ShopItemDTO.from(item, ownedItemIds.contains(item.getId())))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void purchaseItem(Long userId, Long itemId) {
        // Check if already owned
        if (userPurchaseRepository.existsByUserIdAndShopItemId(userId, itemId)) {
            throw new IllegalStateException("Item already owned");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        ShopItem item = shopItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        
        // Check if user has enough balance
        if (user.getBalance().compareTo(item.getPrice()) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        
        // Deduct price from balance
        user.setBalance(user.getBalance().subtract(item.getPrice()));
        userRepository.save(user);
        
        // Create purchase record
        UserPurchase purchase = new UserPurchase();
        purchase.setUser(user);
        purchase.setShopItem(item);
        userPurchaseRepository.save(purchase);
        
        log.info("User {} purchased item {} for {}", user.getUsername(), item.getName(), item.getPrice());
    }
    
    @Transactional
    public void equipItem(Long userId, Long itemId) {
        // Verify ownership
        if (!userPurchaseRepository.existsByUserIdAndShopItemId(userId, itemId)) {
            throw new IllegalStateException("Item not owned");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        ShopItem item = shopItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        
        // Apply customization based on item type
        switch (item.getType()) {
            case PROFILE_PICTURE:
                user.setProfilePicture(item.getItemValue());
                break;
            case NAME_COLOR:
                user.setNameColor(item.getItemValue());
                break;
            case NAMETAG:
                user.setCustomNametag(item.getItemValue());
                break;
        }
        
        userRepository.save(user);
        
        // Note: We can't broadcast to specific race lobby here since we don't track user's current race
        // The frontend will call the participant list again after equipping
        
        log.info("User {} equipped {} ({})", user.getUsername(), item.getName(), item.getType());
    }
}
