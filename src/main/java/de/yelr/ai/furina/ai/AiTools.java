package de.yelr.ai.furina.ai;

import com.google.gson.Gson;
import de.yelr.ai.furina.service.FoodSearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiTools {

    private final FoodSearchService foodSearchService;
    private final Gson gson;

    public AiTools(FoodSearchService foodSearchService, Gson gson) {
        this.foodSearchService = foodSearchService;
        this.gson = gson;
    }

    @Tool(description = "Search restaurants and menu items by keyword and return matching catalog documents")
    public String searchFood(String query) {
        return gson.toJson(foodSearchService.search(query, 5, List.of()));
    }
}
