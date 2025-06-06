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

    PageResponse<FoodSummaryResponse> list(
            String keyword, int page, int size, String sortBy, String direction);

    FoodResponse findById(int id);

    FoodResponse create(FoodCreateRequest request);

    int createBatch(List<FoodCreateRequest> requests);

    FoodResponse update(int id, FoodUpdateRequest request);

    void delete(int id);

    FoodItemResponse addItem(int foodId, FoodItemRequest request);

    FoodItemResponse updateItem(int foodId, int itemId, FoodItemRequest request);

    void deleteItem(int foodId, int itemId);
}
