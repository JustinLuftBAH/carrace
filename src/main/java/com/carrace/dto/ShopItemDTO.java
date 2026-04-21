package com.carrace.dto;

import com.carrace.entity.ShopItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemDTO {
    private Long id;
    private String name;
    private String type;
    private String itemValue;
    private BigDecimal price;
    private String description;
    private boolean owned;
    
    public static ShopItemDTO from(ShopItem item, boolean owned) {
        return new ShopItemDTO(
            item.getId(),
            item.getName(),
            item.getType().name(),
            item.getItemValue(),
            item.getPrice(),
            item.getDescription(),
            owned
        );
    }
}
