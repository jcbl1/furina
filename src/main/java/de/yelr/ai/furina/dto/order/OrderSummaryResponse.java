package de.yelr.ai.furina.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id, String orderNo, String status, BigDecimal totalAmount, LocalDateTime createdAt) {}
