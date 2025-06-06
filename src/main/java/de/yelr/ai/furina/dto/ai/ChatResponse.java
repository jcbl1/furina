package de.yelr.ai.furina.dto.ai;

import java.time.Instant;

public record ChatResponse(String sessionId, String message, Instant time) {}
