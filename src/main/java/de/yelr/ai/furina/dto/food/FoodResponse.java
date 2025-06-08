package de.yelr.ai.furina.dto.food;

import java.math.BigDecimal;
import java.util.List;

public record FoodResponse(
        Integer id,
        String thumbnailPath,
        String title,
        Double rating,
        Integer salesMonthly,
        BigDecimal averageCost,
        Integer deliveryTime,
        String featuredComment,
        List<FoodItemResponse> items) {}
