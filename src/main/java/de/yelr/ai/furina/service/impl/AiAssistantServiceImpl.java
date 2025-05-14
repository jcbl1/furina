package de.yelr.ai.furina.service.impl;

import de.yelr.ai.furina.dto.ai.ChatResponse;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.service.AiAssistantService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public AiAssistantServiceImpl(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    @Override
    public ChatResponse chat(String sessionId, String message) {
        String conversationId = StringUtils.hasText(sessionId)
                ? sessionId
                : UUID.randomUUID().toString();

        String answer = chatClient.prompt()
                .advisors(advisors -> advisors.param(
                        AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,
                        conversationId))
                .user(message)
                .call()
                .content();

        if (!StringUtils.hasText(answer)) {
            throw ApiException.internal("The assistant did not return a response");
        }
        return new ChatResponse(conversationId, answer, Instant.now());
    }

    @Override
    public void clearConversation(String sessionId) {
        if (StringUtils.hasText(sessionId)) {
            chatMemory.clear(sessionId);
        }
    }
}
