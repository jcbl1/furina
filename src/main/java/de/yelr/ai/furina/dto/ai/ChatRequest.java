package de.yelr.ai.furina.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @Size(max = 128, message = "session id must not exceed 128 characters")
        String sessionId,

        @NotBlank(message = "message is required")
        @Size(max = 4_000, message = "message must not exceed 4000 characters")
        String message
) {
}
