package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.common.ApiResponse;
import de.yelr.ai.furina.dto.ai.ChatRequest;
import de.yelr.ai.furina.dto.ai.ChatResponse;
import de.yelr.ai.furina.dto.ai.IndexResponse;
import de.yelr.ai.furina.dto.ai.SearchResultResponse;
import de.yelr.ai.furina.dto.ai.SemanticSearchRequest;
import de.yelr.ai.furina.service.AiAssistantService;
import de.yelr.ai.furina.service.FoodIndexService;
import de.yelr.ai.furina.service.FoodSearchService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiAssistantService assistantService;
    private final FoodSearchService foodSearchService;
    private final FoodIndexService foodIndexService;

    public AiController(
            AiAssistantService assistantService,
            FoodSearchService foodSearchService,
            FoodIndexService foodIndexService) {
        this.assistantService = assistantService;
        this.foodSearchService = foodSearchService;
        this.foodIndexService = foodIndexService;
    }

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ApiResponse.success(assistantService.chat(request.sessionId(), request.message()));
    }

    @PostMapping("/search")
    public ApiResponse<List<SearchResultResponse>> search(
            @Valid @RequestBody SemanticSearchRequest request) {
        int topK = request.topK() == null ? 5 : request.topK();
        return ApiResponse.success(
                foodSearchService.search(request.query(), topK, request.filters()));
    }

    @PostMapping("/index")
    public ApiResponse<IndexResponse> reindex() {
        return ApiResponse.success(new IndexResponse(foodIndexService.reindex()));
    }

    @DeleteMapping("/chat/{sessionId}")
    public ApiResponse<Void> clearConversation(@PathVariable String sessionId) {
        assistantService.clearConversation(sessionId);
        return ApiResponse.success();
    }
}
