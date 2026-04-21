package com.carrace.controller;

import com.carrace.dto.ShopItemDTO;
import com.carrace.entity.ShopItem;
import com.carrace.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShopController {
    
    private final ShopService shopService;
    
    @GetMapping
    public ResponseEntity<List<ShopItemDTO>> getAllItems(@RequestParam Long userId) {
        return ResponseEntity.ok(shopService.getAllShopItems(userId));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ShopItemDTO>> getItemsByType(
            @PathVariable String type,
            @RequestParam Long userId) {
        ShopItem.ItemType itemType = ShopItem.ItemType.valueOf(type);
        return ResponseEntity.ok(shopService.getShopItemsByType(itemType, userId));
    }
    
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, String>> purchaseItem(
            @RequestParam Long userId,
            @RequestParam Long itemId) {
        try {
            shopService.purchaseItem(userId, itemId);
            return ResponseEntity.ok(Map.of("message", "Purchase successful"));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/equip")
    public ResponseEntity<Map<String, String>> equipItem(
            @RequestParam Long userId,
            @RequestParam Long itemId) {
        try {
            shopService.equipItem(userId, itemId);
            return ResponseEntity.ok(Map.of("message", "Item equipped"));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
