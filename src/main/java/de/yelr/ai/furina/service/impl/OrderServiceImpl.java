package de.yelr.ai.furina.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.order.OrderCreateRequest;
import de.yelr.ai.furina.dto.order.OrderResponse;
import de.yelr.ai.furina.dto.order.OrderSummaryResponse;
import de.yelr.ai.furina.entity.CustomerOrder;
import de.yelr.ai.furina.entity.CustomerOrderItem;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.mapper.CustomerOrderItemMapper;
import de.yelr.ai.furina.mapper.CustomerOrderMapper;
import de.yelr.ai.furina.mapper.FoodDataItemMapper;
import de.yelr.ai.furina.mapper.FoodDataMapper;
import de.yelr.ai.furina.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final int DEFAULT_DELIVERY_MINUTES = 30;
    private static final int MAX_PAGE_SIZE = 100;
    private static final DateTimeFormatter ORDER_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final FoodDataMapper foodDataMapper;
    private final FoodDataItemMapper foodDataItemMapper;
    private final CustomerOrderMapper orderMapper;
    private final CustomerOrderItemMapper orderItemMapper;

    public OrderServiceImpl(FoodDataMapper foodDataMapper,
                            FoodDataItemMapper foodDataItemMapper,
                            CustomerOrderMapper orderMapper,
                            CustomerOrderItemMapper orderItemMapper) {
        this.foodDataMapper = foodDataMapper;
        this.foodDataItemMapper = foodDataItemMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderCreateRequest request, Long accountId) {
        LocalDateTime createdAt = LocalDateTime.now();
        int deliveryMinutes = resolveDeliveryMinutes(request.appointment());
        List<CustomerOrderItem> lines = new ArrayList<>();
        List<OrderResponse.OrderLineResponse> responses = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderCreateRequest.RestaurantOrderRequest restaurant : request.restaurants()) {
            FoodData food = requireFood(restaurant.restaurantId());
            for (OrderCreateRequest.OrderLineRequest lineRequest : restaurant.items()) {
                FoodDataItem item = requireItem(food.getId(), lineRequest.itemId());
                BigDecimal lineTotal = item.getPrice()
                        .multiply(BigDecimal.valueOf(lineRequest.quantity()))
                        .setScale(2, RoundingMode.HALF_UP);
                total = total.add(lineTotal);

                CustomerOrderItem line = new CustomerOrderItem();
                line.setFoodDataId(food.getId());
                line.setFoodDataItemId(item.getId());
                line.setItemName(item.getName());
                line.setUnitPrice(item.getPrice());
                line.setQuantity(lineRequest.quantity());
                line.setNote(lineRequest.note());
                lines.add(line);

                responses.add(new OrderResponse.OrderLineResponse(
                        food.getId(),
                        item.getId(),
                        item.getName(),
                        lineRequest.quantity(),
                        item.getPrice(),
                        lineTotal,
                        lineRequest.note()
                ));
            }
        }

        CustomerOrder order = new CustomerOrder();
        order.setOrderNo(generateOrderNo(createdAt));
        order.setAccountId(accountId);
        order.setImmediate(Boolean.TRUE.equals(request.appointment().immediate()));
        order.setMinutesFromNow(request.appointment().minutesFromNow());
        order.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        order.setStatus("PLACED");
        order.setCreatedAt(createdAt);
        orderMapper.insert(order);

        for (CustomerOrderItem line : lines) {
            line.setOrderId(order.getId());
            orderItemMapper.insert(line);
        }

        return new OrderResponse(
                order.getOrderNo(),
                order.getStatus(),
                order.getTotalAmount(),
                createdAt.plusMinutes(deliveryMinutes),
                createdAt,
                responses
        );
    }

    @Override
    public PageResponse<OrderSummaryResponse> listForAccount(Long accountId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        var query = Wrappers.<CustomerOrder>lambdaQuery()
                .eq(CustomerOrder::getAccountId, accountId)
                .orderByDesc(CustomerOrder::getCreatedAt);
        Page<CustomerOrder> result = orderMapper.selectPage(new Page<>(safePage, safeSize), query);
        List<OrderSummaryResponse> items = result.getRecords().stream()
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getOrderNo(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getCreatedAt()))
                .toList();
        return PageResponse.of(items, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public OrderResponse findByOrderNo(String orderNo, Long accountId) {
        CustomerOrder order = orderMapper.selectOne(
                Wrappers.<CustomerOrder>lambdaQuery().eq(CustomerOrder::getOrderNo, orderNo));
        if (order == null || (accountId != null && !accountId.equals(order.getAccountId()))) {
            throw ApiException.notFound("Order not found");
        }
        List<CustomerOrderItem> items = orderItemMapper.selectList(
                Wrappers.<CustomerOrderItem>lambdaQuery().eq(CustomerOrderItem::getOrderId, order.getId()));
        List<OrderResponse.OrderLineResponse> lines = items.stream()
                .map(item -> new OrderResponse.OrderLineResponse(
                        item.getFoodDataId(),
                        item.getFoodDataItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                                .setScale(2, RoundingMode.HALF_UP),
                        item.getNote()))
                .toList();
        return new OrderResponse(
                order.getOrderNo(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt().plusMinutes(resolveDeliveryMinutes(
                        new OrderCreateRequest.AppointmentRequest(
                                order.getImmediate(), order.getMinutesFromNow()))),
                order.getCreatedAt(),
                lines
        );
    }

    private FoodData requireFood(Integer id) {
        FoodData food = foodDataMapper.selectById(id);
        if (food == null) {
            throw ApiException.notFound("Restaurant %d not found".formatted(id));
        }
        return food;
    }

    private FoodDataItem requireItem(Integer restaurantId, Integer itemId) {
        FoodDataItem item = foodDataItemMapper.selectOne(Wrappers.<FoodDataItem>lambdaQuery()
                .eq(FoodDataItem::getId, itemId)
                .eq(FoodDataItem::getFoodDataId, restaurantId));
        if (item == null) {
            throw ApiException.notFound(
                    "Menu item %d not found for restaurant %d".formatted(itemId, restaurantId));
        }
        return item;
    }

    private static int resolveDeliveryMinutes(OrderCreateRequest.AppointmentRequest appointment) {
        if (appointment != null && !Boolean.TRUE.equals(appointment.immediate())
                && appointment.minutesFromNow() != null) {
            return appointment.minutesFromNow();
        }
        return DEFAULT_DELIVERY_MINUTES;
    }

    private static String generateOrderNo(LocalDateTime createdAt) {
        return "FUR" + createdAt.format(ORDER_NO_FORMAT)
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
