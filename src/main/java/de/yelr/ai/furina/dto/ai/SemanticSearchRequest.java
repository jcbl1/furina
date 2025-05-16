package de.yelr.ai.furina.dto.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SemanticSearchRequest(
        @NotBlank(message = "query is required")
        @Size(max = 500, message = "query must not exceed 500 characters")
        String query,

        @Min(value = 1, message = "top_k must be at least 1")
        @Max(value = 20, message = "top_k must not exceed 20")
        Integer topK,

        List<@Valid SearchFilter> filters
) {
}
