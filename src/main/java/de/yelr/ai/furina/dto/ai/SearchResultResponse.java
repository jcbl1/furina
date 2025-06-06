package de.yelr.ai.furina.dto.ai;

import java.util.Map;

public record SearchResultResponse(
        String id, String text, Map<String, Object> metadata, Double score) {}
