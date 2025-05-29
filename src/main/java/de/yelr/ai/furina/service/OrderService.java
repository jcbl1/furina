package de.yelr.ai.furina.service;

import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.order.OrderCreateRequest;
import de.yelr.ai.furina.dto.order.OrderResponse;
import de.yelr.ai.furina.dto.order.OrderSummaryResponse;

public interface OrderService {

    OrderResponse placeOrder(OrderCreateRequest request, Long accountId);

    PageResponse<OrderSummaryResponse> listForAccount(Long accountId, int page, int size);

    OrderResponse findByOrderNo(String orderNo, Long accountId);
}
