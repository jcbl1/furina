package de.yelr.ai.furina.converter;

import de.yelr.ai.furina.dto.food.FoodItemResponse;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.dto.food.FoodSummaryResponse;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import java.util.List;

/**
 * Translates persistence entities into API response models. Keeping this mapping outside the DTOs
 * lets the boundary models stay independent of MyBatis.
 */
public final class FoodDtoConverter {

    private FoodDtoConverter() {}

    public static FoodItemResponse toItemResponse(FoodDataItem item) {
        return new FoodItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getPrice());
    }

    public static FoodSummaryResponse toSummaryResponse(FoodData food) {
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

    public static FoodResponse toResponse(FoodData food, List<FoodDataItem> items) {
        List<FoodItemResponse> itemResponses =
                items == null
                        ? List.of()
                        : items.stream().map(FoodDtoConverter::toItemResponse).toList();
        return new FoodResponse(
                food.getId(),
                food.getThumbnailPath(),
                food.getTitle(),
                food.getRating(),
                food.getSalesMonthly(),
                food.getAverageCost(),
                food.getDeliveryTime(),
                food.getFeaturedComment(),
                itemResponses);
    }
}
