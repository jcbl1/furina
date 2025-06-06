package de.yelr.ai.furina.dto.food;

import de.yelr.ai.furina.entity.FoodDataItem;
import java.math.BigDecimal;

public record FoodItemResponse(Integer id, String name, String description, BigDecimal price) {

    public static FoodItemResponse from(FoodDataItem item) {
        return new FoodItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getPrice());
    }
}
