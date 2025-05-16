package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.common.ApiResponse;
import de.yelr.ai.furina.dto.ai.ChatRequest;
import de.yelr.ai.furina.dto.ai.ChatResponse;
import de.yelr.ai.furina.dto.ai.SearchResultResponse;
import de.yelr.ai.furina.dto.ai.SemanticSearchRequest;
import de.yelr.ai.furina.service.AiAssistantService;
import de.yelr.ai.furina.service.FoodSearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiAssistantService assistantService;
    private final FoodSearchService foodSearchService;

    public AiController(AiAssistantService assistantService, FoodSearchService foodSearchService) {
        this.assistantService = assistantService;
        this.foodSearchService = foodSearchService;
    }

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ApiResponse.success(assistantService.chat(request.sessionId(), request.message()));
    }

    @PostMapping("/search")
    public ApiResponse<List<SearchResultResponse>> search(@Valid @RequestBody SemanticSearchRequest request) {
        int topK = request.topK() == null ? 5 : request.topK();
        return ApiResponse.success(
                foodSearchService.search(request.query(), topK, request.filters()));
    }

    @DeleteMapping("/chat/{sessionId}")
    public ApiResponse<Void> clearConversation(@PathVariable String sessionId) {
        assistantService.clearConversation(sessionId);
        return ApiResponse.success();
    }
}
