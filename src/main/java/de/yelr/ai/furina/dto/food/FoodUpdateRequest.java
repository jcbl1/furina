package de.yelr.ai.furina.dto.food;

import java.math.BigDecimal;

public record FoodUpdateRequest(
        String thumbnailPath,
        String title,
        Double rating,
        Integer salesMonthly,
        BigDecimal averageCost,
        Integer deliveryTime,
        String featuredComment
) {
}
