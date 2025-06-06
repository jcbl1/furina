package de.yelr.ai.furina.dto.food;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record FoodCreateRequest(
        @Size(max = 255, message = "thumbnail path must not exceed 255 characters")
                String thumbnailPath,
        @NotBlank(message = "title is required")
                @Size(max = 255, message = "title must not exceed 255 characters")
                String title,
        @DecimalMin(value = "0.0", message = "rating must be between 0 and 5")
                @DecimalMax(value = "5.0", message = "rating must be between 0 and 5")
                Double rating,
        @Min(value = 0, message = "monthly sales must not be negative") Integer salesMonthly,
        @DecimalMin(value = "0.0", message = "average cost must not be negative")
                @Digits(
                        integer = 8,
                        fraction = 2,
                        message = "average cost must have at most 2 decimal places")
                BigDecimal averageCost,
        @Min(value = 0, message = "delivery time must not be negative") Integer deliveryTime,
        @Size(max = 512, message = "featured comment must not exceed 512 characters")
                String featuredComment,
        List<@Valid FoodItemRequest> items) {}
