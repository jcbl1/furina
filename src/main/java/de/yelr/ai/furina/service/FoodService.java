package de.yelr.ai.furina.service;

import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.food.FoodCreateRequest;
import de.yelr.ai.furina.dto.food.FoodItemRequest;
import de.yelr.ai.furina.dto.food.FoodItemResponse;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.dto.food.FoodSummaryResponse;
import de.yelr.ai.furina.dto.food.FoodUpdateRequest;

import java.util.List;

public interface FoodService {

    PageResponse<FoodSummaryResponse> list(String keyword, int page, int size, String sortBy, String direction);

    /**
     * @return the food with its items, or {@code null} when it does not exist
     */
    FoodResponse findById(int id);

    FoodResponse create(FoodCreateRequest request);

    int createBatch(List<FoodCreateRequest> requests);

    /**
     * @return the updated food, or {@code null} when it does not exist
     */
    FoodResponse update(int id, FoodUpdateRequest request);

    boolean delete(int id);

    /**
     * @return the inserted item, or {@code null} when the food does not exist
     */
    FoodItemResponse addItem(int foodId, FoodItemRequest request);

    /**
     * @return the updated item, or {@code null} when it does not belong to the food
     */
    FoodItemResponse updateItem(int foodId, int itemId, FoodItemRequest request);

    boolean deleteItem(int foodId, int itemId);
}
