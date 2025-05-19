package de.yelr.ai.furina.config;

import com.google.gson.Gson;
import de.yelr.ai.furina.ai.AiTools;
import de.yelr.ai.furina.util.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ChatMemory chatMemory,
                                 DateTimeTools dateTimeTools,
                                 AiTools aiTools) {
        return builder
                .defaultSystem(new ClassPathResource("prompts/food-assistant.st"))
                .defaultTools(dateTimeTools, aiTools)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }
}
