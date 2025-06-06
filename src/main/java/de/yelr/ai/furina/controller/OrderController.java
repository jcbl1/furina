package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.common.ApiResponse;
import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.order.OrderCreateRequest;
import de.yelr.ai.furina.dto.order.OrderResponse;
import de.yelr.ai.furina.dto.order.OrderSummaryResponse;
import de.yelr.ai.furina.security.AccountPrincipal;
import de.yelr.ai.furina.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> place(
            @Valid @RequestBody OrderCreateRequest request, Authentication authentication) {
        Long accountId = ((AccountPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.placeOrder(request, accountId)));
    }

    @GetMapping
    public ApiResponse<PageResponse<OrderSummaryResponse>> list(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            Authentication authentication) {
        Long accountId = ((AccountPrincipal) authentication.getPrincipal()).getId();
        return ApiResponse.success(orderService.listForAccount(accountId, page, size));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<OrderResponse> detail(
            @PathVariable String orderNo, Authentication authentication) {
        Long accountId = ((AccountPrincipal) authentication.getPrincipal()).getId();
        return ApiResponse.success(orderService.findByOrderNo(orderNo, accountId));
    }
}
