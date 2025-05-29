package de.yelr.ai.furina.service;

import de.yelr.ai.furina.dto.order.OrderCreateRequest;
import de.yelr.ai.furina.dto.order.OrderResponse;
import de.yelr.ai.furina.entity.CustomerOrder;
import de.yelr.ai.furina.entity.CustomerOrderItem;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.mapper.CustomerOrderItemMapper;
import de.yelr.ai.furina.mapper.CustomerOrderMapper;
import de.yelr.ai.furina.mapper.FoodDataItemMapper;
import de.yelr.ai.furina.mapper.FoodDataMapper;
import de.yelr.ai.furina.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private FoodDataMapper foodDataMapper;

    @Mock
    private FoodDataItemMapper foodDataItemMapper;

    @Mock
    private CustomerOrderMapper orderMapper;

    @Mock
    private CustomerOrderItemMapper orderItemMapper;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                foodDataMapper, foodDataItemMapper, orderMapper, orderItemMapper);
    }

    @Test
    void calculatesTotalsAndPersistsLines() {
        FoodData food = food(3, "Noodle shop");
        FoodDataItem noodles = item(10, 3, "Beef noodles", "10.00");
        FoodDataItem egg = item(11, 3, "Egg", "7.50");
        when(foodDataMapper.selectById(3)).thenReturn(food);
        when(foodDataItemMapper.selectOne(any())).thenReturn(noodles, egg);
        when(orderMapper.insert(any(CustomerOrder.class))).thenAnswer(invocation -> {
            invocation.<CustomerOrder>getArgument(0).setId(100L);
            return 1;
        });
        when(orderItemMapper.insert(any(CustomerOrderItem.class))).thenReturn(1);

        OrderResponse response = orderService.placeOrder(new OrderCreateRequest(
                new OrderCreateRequest.AppointmentRequest(false, 45),
                List.of(new OrderCreateRequest.RestaurantOrderRequest(3, List.of(
                        new OrderCreateRequest.OrderLineRequest(10, 2, "no coriander"),
                        new OrderCreateRequest.OrderLineRequest(11, 1, null)
                )))
        ), 9L);

        assertThat(response.totalAmount()).isEqualByComparingTo("27.50");
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).lineTotal()).isEqualByComparingTo("20.00");
        assertThat(response.eta()).isEqualTo(response.createdAt().plusMinutes(45));
        assertThat(response.status()).isEqualTo("PLACED");

        ArgumentCaptor<CustomerOrder> orderCaptor = ArgumentCaptor.forClass(CustomerOrder.class);
        verify(orderMapper).insert(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getAccountId()).isEqualTo(9L);
        assertThat(orderCaptor.getValue().getTotalAmount()).isEqualByComparingTo("27.50");
        verify(orderItemMapper, org.mockito.Mockito.times(2)).insert(any(CustomerOrderItem.class));
    }

    @Test
    void rejectsItemOwnedByAnotherRestaurant() {
        when(foodDataMapper.selectById(3)).thenReturn(food(3, "Noodle shop"));
        when(foodDataItemMapper.selectOne(any())).thenReturn(null);

        assertThatThrownBy(() -> orderService.placeOrder(new OrderCreateRequest(
                new OrderCreateRequest.AppointmentRequest(true, null),
                List.of(new OrderCreateRequest.RestaurantOrderRequest(3, List.of(
                        new OrderCreateRequest.OrderLineRequest(99, 1, null)
                )))
        ), 9L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Menu item 99");
    }

    @Test
    void hidesOrdersBelongingToAnotherAccount() {
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setOrderNo("FUR1");
        order.setAccountId(2L);
        when(orderMapper.selectOne(any())).thenReturn(order);

        assertThatThrownBy(() -> orderService.findByOrderNo("FUR1", 1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Order not found");
    }

    private static FoodData food(int id, String title) {
        FoodData food = new FoodData();
        food.setId(id);
        food.setTitle(title);
        return food;
    }

    private static FoodDataItem item(int id, int foodId, String name, String price) {
        FoodDataItem item = new FoodDataItem();
        item.setId(id);
        item.setFoodDataId(foodId);
        item.setName(name);
        item.setPrice(new BigDecimal(price));
        return item;
    }
}
