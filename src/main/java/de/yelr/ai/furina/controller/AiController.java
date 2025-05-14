package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.common.ApiResponse;
import de.yelr.ai.furina.dto.ai.ChatRequest;
import de.yelr.ai.furina.dto.ai.ChatResponse;
import de.yelr.ai.furina.service.AiAssistantService;
import jakarta.validation.Valid;
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

    public AiController(AiAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ApiResponse.success(assistantService.chat(request.sessionId(), request.message()));
    }

    @DeleteMapping("/chat/{sessionId}")
    public ApiResponse<Void> clearConversation(@PathVariable String sessionId) {
        assistantService.clearConversation(sessionId);
        return ApiResponse.success();
    }
}
