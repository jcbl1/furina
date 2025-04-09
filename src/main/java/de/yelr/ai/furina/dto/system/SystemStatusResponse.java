package de.yelr.ai.furina.dto.system;

import java.time.Instant;

public record SystemStatusResponse(String status, String application, Instant time) {
}
