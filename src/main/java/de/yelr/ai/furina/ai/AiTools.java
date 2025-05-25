package de.yelr.ai.furina.ai;

import com.google.gson.Gson;
import de.yelr.ai.furina.dto.order.OrderCreateRequest;
import de.yelr.ai.furina.service.FoodSearchService;
import de.yelr.ai.furina.service.OrderService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiTools {

    private final FoodSearchService foodSearchService;
    private final OrderService orderService;
    private final Gson gson;

    public AiTools(FoodSearchService foodSearchService, OrderService orderService, Gson gson) {
        this.foodSearchService = foodSearchService;
        this.orderService = orderService;
        this.gson = gson;
    }

    @Tool(description = "Search restaurants and menu items by keyword and return matching catalog documents")
    public String searchFood(String query) {
        return gson.toJson(foodSearchService.search(query, 5, List.of()));
    }

    @Tool(description = "Place a food order. Only use restaurant and item ids that came from search results.")
    public String placeOrder(OrderCreateRequest request) {
        return gson.toJson(orderService.placeOrder(request, null));
    }
}
