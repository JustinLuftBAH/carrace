package com.carrace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "shop_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;
    
    @Column(nullable = false)
    private String itemValue; // The actual emoji, color hex, or nametag text
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(length = 500)
    private String description;
    
    public enum ItemType {
        PROFILE_PICTURE,
        NAME_COLOR,
        NAMETAG
    }
}
