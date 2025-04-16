package de.yelr.ai.furina.dto.food;

import java.math.BigDecimal;

public record FoodItemRequest(
        String name,
        String description,
        BigDecimal price
) {
}
