package com.carrace.config;

import com.carrace.entity.ShopItem;
import com.carrace.entity.User;
import com.carrace.repository.ShopItemRepository;
import com.carrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Initializes the database with sample data for easy testing.
 * Creates 3 demo users with starting balances and populates shop items.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, ShopItemRepository shopItemRepository) {
        return args -> {
            // Initialize users if needed
            try {
                if (userRepository.count() == 0) {
                    log.info("Initializing sample users...");
                    
                    User alice = new User("alice", new BigDecimal("1000.00"));
                    User bob = new User("bob", new BigDecimal("1000.00"));
                    User charlie = new User("charlie", new BigDecimal("1000.00"));
                    
                    userRepository.save(alice);
                    userRepository.save(bob);
                    userRepository.save(charlie);
                    
                    log.info("Created 3 sample users: alice, bob, charlie (each with $1000 balance)");
                    log.info("User IDs: Alice={}, Bob={}, Charlie={}", 
                        alice.getId(), bob.getId(), charlie.getId());
                }
            } catch (Exception e) {
                log.warn("Could not initialize users: {}", e.getMessage());
            }
            
            // Initialize shop items if needed
            try {
                // Try to find a specific item to check if shop is initialized
                // This will trigger table creation if needed
                List<ShopItem> existingItems = shopItemRepository.findAll();
                
                if (existingItems.isEmpty()) {
                    log.info("Initializing shop items...");
                    
                    // Profile Pictures - 10 items with varied prices
                    shopItemRepository.save(new ShopItem(null, "Racing Car", ShopItem.ItemType.PROFILE_PICTURE, "🏎️", new BigDecimal("0"), "Default racing car"));
                    shopItemRepository.save(new ShopItem(null, "Fire", ShopItem.ItemType.PROFILE_PICTURE, "🔥", new BigDecimal("150"), "Hot and spicy!"));
                    shopItemRepository.save(new ShopItem(null, "Crown", ShopItem.ItemType.PROFILE_PICTURE, "👑", new BigDecimal("500"), "Royal status"));
                    shopItemRepository.save(new ShopItem(null, "Lightning", ShopItem.ItemType.PROFILE_PICTURE, "⚡", new BigDecimal("200"), "Speed of light"));
                    shopItemRepository.save(new ShopItem(null, "Trophy", ShopItem.ItemType.PROFILE_PICTURE, "🏆", new BigDecimal("400"), "For winners"));
                    shopItemRepository.save(new ShopItem(null, "Star", ShopItem.ItemType.PROFILE_PICTURE, "⭐", new BigDecimal("250"), "Shine bright"));
                    shopItemRepository.save(new ShopItem(null, "Rocket", ShopItem.ItemType.PROFILE_PICTURE, "🚀", new BigDecimal("300"), "To the moon!"));
                    shopItemRepository.save(new ShopItem(null, "Diamond", ShopItem.ItemType.PROFILE_PICTURE, "💎", new BigDecimal("800"), "Ultra rare"));
                    shopItemRepository.save(new ShopItem(null, "Skull", ShopItem.ItemType.PROFILE_PICTURE, "💀", new BigDecimal("350"), "Danger zone"));
                    shopItemRepository.save(new ShopItem(null, "Alien", ShopItem.ItemType.PROFILE_PICTURE, "👽", new BigDecimal("600"), "Out of this world"));
                    
                    // Premium Profile Pictures (5x more expensive - $2500-$5000)
                    shopItemRepository.save(new ShopItem(null, "Dragon", ShopItem.ItemType.PROFILE_PICTURE, "🐉", new BigDecimal("2500"), "Mythical beast"));
                    shopItemRepository.save(new ShopItem(null, "Phoenix", ShopItem.ItemType.PROFILE_PICTURE, "🦅", new BigDecimal("3000"), "Rise from ashes"));
                    shopItemRepository.save(new ShopItem(null, "Unicorn", ShopItem.ItemType.PROFILE_PICTURE, "🦄", new BigDecimal("3500"), "Magical legend"));
                    shopItemRepository.save(new ShopItem(null, "Rainbow", ShopItem.ItemType.PROFILE_PICTURE, "🌈", new BigDecimal("2800"), "Spectrum of color"));
                    shopItemRepository.save(new ShopItem(null, "Explosion", ShopItem.ItemType.PROFILE_PICTURE, "💥", new BigDecimal("4000"), "Explosive power"));
                    shopItemRepository.save(new ShopItem(null, "Galaxy", ShopItem.ItemType.PROFILE_PICTURE, "🌌", new BigDecimal("4500"), "Cosmic wonder"));
                    shopItemRepository.save(new ShopItem(null, "Crystal", ShopItem.ItemType.PROFILE_PICTURE, "🔮", new BigDecimal("3800"), "Mystic crystal"));
                    shopItemRepository.save(new ShopItem(null, "Infinity", ShopItem.ItemType.PROFILE_PICTURE, "♾️", new BigDecimal("5000"), "Limitless"));
                    shopItemRepository.save(new ShopItem(null, "Demon", ShopItem.ItemType.PROFILE_PICTURE, "😈", new BigDecimal("3200"), "Dark power"));
                    shopItemRepository.save(new ShopItem(null, "Angel", ShopItem.ItemType.PROFILE_PICTURE, "😇", new BigDecimal("3200"), "Divine grace"));
                    
                    // Name Colors - 10 items
                    shopItemRepository.save(new ShopItem(null, "White", ShopItem.ItemType.NAME_COLOR, "#FFFFFF", new BigDecimal("0"), "Default white"));
                    shopItemRepository.save(new ShopItem(null, "Red", ShopItem.ItemType.NAME_COLOR, "#FF4444", new BigDecimal("100"), "Bold red"));
                    shopItemRepository.save(new ShopItem(null, "Blue", ShopItem.ItemType.NAME_COLOR, "#4444FF", new BigDecimal("100"), "Cool blue"));
                    shopItemRepository.save(new ShopItem(null, "Green", ShopItem.ItemType.NAME_COLOR, "#44FF44", new BigDecimal("100"), "Fresh green"));
                    shopItemRepository.save(new ShopItem(null, "Gold", ShopItem.ItemType.NAME_COLOR, "#FFD700", new BigDecimal("300"), "Golden glory"));
                    shopItemRepository.save(new ShopItem(null, "Purple", ShopItem.ItemType.NAME_COLOR, "#AA44FF", new BigDecimal("150"), "Royal purple"));
                    shopItemRepository.save(new ShopItem(null, "Cyan", ShopItem.ItemType.NAME_COLOR, "#00FFFF", new BigDecimal("150"), "Electric cyan"));
                    shopItemRepository.save(new ShopItem(null, "Pink", ShopItem.ItemType.NAME_COLOR, "#FF69B4", new BigDecimal("150"), "Hot pink"));
                    shopItemRepository.save(new ShopItem(null, "Orange", ShopItem.ItemType.NAME_COLOR, "#FF8C00", new BigDecimal("120"), "Blazing orange"));
                    shopItemRepository.save(new ShopItem(null, "Silver", ShopItem.ItemType.NAME_COLOR, "#C0C0C0", new BigDecimal("200"), "Metallic silver"));
                    
                    // Premium Name Colors (5x more expensive - $1500-$2000)
                    shopItemRepository.save(new ShopItem(null, "Rainbow Gradient", ShopItem.ItemType.NAME_COLOR, "linear-gradient(90deg, #FF0000, #FF7F00, #FFFF00, #00FF00, #0000FF, #4B0082, #9400D3)", new BigDecimal("2000"), "Full spectrum"));
                    shopItemRepository.save(new ShopItem(null, "Lava", ShopItem.ItemType.NAME_COLOR, "#FF4500", new BigDecimal("1500"), "Molten hot"));
                    shopItemRepository.save(new ShopItem(null, "Neon Green", ShopItem.ItemType.NAME_COLOR, "#39FF14", new BigDecimal("1500"), "Ultra bright"));
                    shopItemRepository.save(new ShopItem(null, "Electric Blue", ShopItem.ItemType.NAME_COLOR, "#7DF9FF", new BigDecimal("1500"), "Lightning flash"));
                    shopItemRepository.save(new ShopItem(null, "Royal Gold", ShopItem.ItemType.NAME_COLOR, "#FFD700", new BigDecimal("1800"), "King's treasure"));
                    shopItemRepository.save(new ShopItem(null, "Platinum", ShopItem.ItemType.NAME_COLOR, "#E5E4E2", new BigDecimal("1700"), "Precious metal"));
                    shopItemRepository.save(new ShopItem(null, "Ruby Red", ShopItem.ItemType.NAME_COLOR, "#E0115F", new BigDecimal("1600"), "Precious gem"));
                    shopItemRepository.save(new ShopItem(null, "Emerald", ShopItem.ItemType.NAME_COLOR, "#50C878", new BigDecimal("1600"), "Rare jewel"));
                    shopItemRepository.save(new ShopItem(null, "Sapphire", ShopItem.ItemType.NAME_COLOR, "#0F52BA", new BigDecimal("1600"), "Deep blue gem"));
                    shopItemRepository.save(new ShopItem(null, "Cosmic Purple", ShopItem.ItemType.NAME_COLOR, "#B000FF", new BigDecimal("1900"), "Space magic"));
                    
                    // Premium Nametags (5x more expensive - $5000+)
                    shopItemRepository.save(new ShopItem(null, "God of Speed", ShopItem.ItemType.NAMETAG, "God of Speed", new BigDecimal("5000"), "Divine velocity"));
                    shopItemRepository.save(new ShopItem(null, "Immortal", ShopItem.ItemType.NAMETAG, "Immortal", new BigDecimal("4500"), "Never dies"));
                    shopItemRepository.save(new ShopItem(null, "Supreme", ShopItem.ItemType.NAMETAG, "Supreme", new BigDecimal("4000"), "Ultimate authority"));
                    shopItemRepository.save(new ShopItem(null, "Mythic", ShopItem.ItemType.NAMETAG, "Mythic", new BigDecimal("3500"), "Legendary status"));
                    shopItemRepository.save(new ShopItem(null, "Apex Predator", ShopItem.ItemType.NAMETAG, "Apex Predator", new BigDecimal("4200"), "Top of food chain"));
                    shopItemRepository.save(new ShopItem(null, "Unstoppable", ShopItem.ItemType.NAMETAG, "Unstoppable", new BigDecimal("3800"), "Infinite momentum"));
                    shopItemRepository.save(new ShopItem(null, "Elite", ShopItem.ItemType.NAMETAG, "Elite", new BigDecimal("3000"), "Top 1%"));
                    shopItemRepository.save(new ShopItem(null, "Grandmaster", ShopItem.ItemType.NAMETAG, "Grandmaster", new BigDecimal("4800"), "Master of masters"));
                    shopItemRepository.save(new ShopItem(null, "Infinity Racer", ShopItem.ItemType.NAMETAG, "Infinity Racer", new BigDecimal("5000"), "Beyond limits"));
                    shopItemRepository.save(new ShopItem(null, "The One", ShopItem.ItemType.NAMETAG, "The One", new BigDecimal("6000"), "None above"));
                    
                    // Nametags - 10 items
                    shopItemRepository.save(new ShopItem(null, "Newbie", ShopItem.ItemType.NAMETAG, "Newbie", new BigDecimal("0"), "Just started"));
                    shopItemRepository.save(new ShopItem(null, "Rookie", ShopItem.ItemType.NAMETAG, "Rookie", new BigDecimal("100"), "Getting started"));
                    shopItemRepository.save(new ShopItem(null, "Pro Racer", ShopItem.ItemType.NAMETAG, "Pro Racer", new BigDecimal("250"), "Professional status"));
                    shopItemRepository.save(new ShopItem(null, "Speed Demon", ShopItem.ItemType.NAMETAG, "Speed Demon", new BigDecimal("300"), "Need for speed"));
                    shopItemRepository.save(new ShopItem(null, "Champion", ShopItem.ItemType.NAMETAG, "Champion", new BigDecimal("500"), "Tournament winner"));
                    shopItemRepository.save(new ShopItem(null, "Legend", ShopItem.ItemType.NAMETAG, "Legend", new BigDecimal("800"), "Legendary racer"));
                    shopItemRepository.save(new ShopItem(null, "Turbo", ShopItem.ItemType.NAMETAG, "Turbo", new BigDecimal("200"), "Turbocharged"));
                    shopItemRepository.save(new ShopItem(null, "Nitro", ShopItem.ItemType.NAMETAG, "Nitro", new BigDecimal("350"), "Nitro boost"));
                    shopItemRepository.save(new ShopItem(null, "High Roller", ShopItem.ItemType.NAMETAG, "High Roller", new BigDecimal("600"), "Big money bets"));
                    shopItemRepository.save(new ShopItem(null, "Untouchable", ShopItem.ItemType.NAMETAG, "Untouchable", new BigDecimal("1000"), "Unbeatable"));
                    
                    long count = shopItemRepository.count();
                    log.info("Created {} shop items", count);
                } else {
                    log.info("Shop already initialized with {} items", existingItems.size());
                }
            } catch (Exception e) {
                log.warn("Could not initialize shop items: {}", e.getMessage());
            }
        };
    }
}
