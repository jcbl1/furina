package de.yelr.ai.furina.dto.food;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FoodItemRequest(
        @NotBlank(message = "item name is required")
        @Size(max = 255, message = "item name must not exceed 255 characters")
        String name,

        @Size(max = 512, message = "item description must not exceed 512 characters")
        String description,

        @NotNull(message = "item price is required")
        @DecimalMin(value = "0.0", message = "item price must not be negative")
        @Digits(integer = 8, fraction = 2, message = "item price must have at most 2 decimal places")
        BigDecimal price
) {
}
