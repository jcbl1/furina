package de.yelr.ai.furina.dto.food;

import de.yelr.ai.furina.entity.FoodData;
import java.math.BigDecimal;

public record FoodSummaryResponse(
        Integer id,
        String thumbnailPath,
        String title,
        Double rating,
        Integer salesMonthly,
        BigDecimal averageCost,
        Integer deliveryTime,
        String featuredComment) {

    public static FoodSummaryResponse from(FoodData food) {
        return new FoodSummaryResponse(
                food.getId(),
                food.getThumbnailPath(),
                food.getTitle(),
                food.getRating(),
                food.getSalesMonthly(),
                food.getAverageCost(),
                food.getDeliveryTime(),
                food.getFeaturedComment());
    }
}
