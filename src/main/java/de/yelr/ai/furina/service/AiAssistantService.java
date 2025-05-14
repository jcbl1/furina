package de.yelr.ai.furina.service;

import de.yelr.ai.furina.dto.ai.ChatResponse;

public interface AiAssistantService {

    ChatResponse chat(String sessionId, String message);

    void clearConversation(String sessionId);
}
