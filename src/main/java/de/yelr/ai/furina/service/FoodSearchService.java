package de.yelr.ai.furina.service;

import de.yelr.ai.furina.dto.ai.SearchFilter;
import de.yelr.ai.furina.dto.ai.SearchResultResponse;
import java.util.List;

public interface FoodSearchService {

    List<SearchResultResponse> search(String query, int topK, List<SearchFilter> filters);
}
