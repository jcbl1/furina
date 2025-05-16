package de.yelr.ai.furina.dto.ai;

import jakarta.validation.constraints.NotBlank;

public record SearchFilter(
        @NotBlank(message = "filter field is required")
        String field,

        @NotBlank(message = "filter operator is required")
        String operator,

        @NotBlank(message = "filter value is required")
        String value
) {
}
