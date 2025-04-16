package de.yelr.ai.furina.service;

import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.food.FoodCreateRequest;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.dto.food.FoodSummaryResponse;

import java.util.List;

public interface FoodService {

    PageResponse<FoodSummaryResponse> list(String keyword, int page, int size, String sortBy, String direction);

    /**
     * @return the food with its items, or {@code null} when it does not exist
     */
    FoodResponse findById(int id);

    FoodResponse create(FoodCreateRequest request);

    int createBatch(List<FoodCreateRequest> requests);
}
