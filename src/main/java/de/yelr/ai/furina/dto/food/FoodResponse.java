package de.yelr.ai.furina.dto.food;

import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;

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
        List<FoodItemResponse> items
) {

    public static FoodResponse from(FoodData food, List<FoodDataItem> items) {
        List<FoodItemResponse> itemResponses = items == null
                ? List.of()
                : items.stream().map(FoodItemResponse::from).toList();
        return new FoodResponse(
                food.getId(),
                food.getThumbnailPath(),
                food.getTitle(),
                food.getRating(),
                food.getSalesMonthly(),
                food.getAverageCost(),
                food.getDeliveryTime(),
                food.getFeaturedComment(),
                itemResponses
        );
    }
}
