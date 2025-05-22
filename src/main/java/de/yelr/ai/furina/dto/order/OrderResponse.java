package de.yelr.ai.furina.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderNo,
        String status,
        BigDecimal totalAmount,
        LocalDateTime eta,
        LocalDateTime createdAt,
        List<OrderLineResponse> items
) {

    public record OrderLineResponse(
            Integer restaurantId,
            Integer itemId,
            String name,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal,
            String note
    ) {
    }
}
