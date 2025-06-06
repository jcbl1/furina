package de.yelr.ai.furina.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderCreateRequest(
        @NotNull(message = "appointment is required") @Valid AppointmentRequest appointment,
        @NotEmpty(message = "at least one restaurant is required")
                List<@Valid RestaurantOrderRequest> restaurants) {

    public record AppointmentRequest(
            @NotNull(message = "immediate is required") Boolean immediate,
            @Min(value = 0, message = "minutes_from_now must not be negative")
                    Integer minutesFromNow) {}

    public record RestaurantOrderRequest(
            @NotNull(message = "restaurant_id is required") Integer restaurantId,
            @NotEmpty(message = "at least one order line is required")
                    List<@Valid OrderLineRequest> items) {}

    public record OrderLineRequest(
            @NotNull(message = "item_id is required") Integer itemId,
            @NotNull(message = "quantity is required")
                    @Min(value = 1, message = "quantity must be at least 1")
                    Integer quantity,
            String note) {}
}
