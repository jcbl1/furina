package de.yelr.ai.furina.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import de.yelr.ai.furina.dto.food.FoodCreateRequest;
import de.yelr.ai.furina.dto.food.FoodItemRequest;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.dto.food.FoodUpdateRequest;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.mapper.FoodDataItemMapper;
import de.yelr.ai.furina.mapper.FoodDataMapper;
import de.yelr.ai.furina.service.impl.FoodServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceImplTest {

    @Mock
    private FoodDataMapper foodDataMapper;

    @Mock
    private FoodDataItemMapper foodDataItemMapper;

    private FoodServiceImpl foodService;

    @BeforeEach
    void setUp() {
        foodService = new FoodServiceImpl(foodDataMapper, foodDataItemMapper);
    }

    @Test
    void listReturnsMappedPage() {
        FoodData food = food(1, "Noodle shop");
        Page<FoodData> page = new Page<>(1, 10);
        page.setRecords(List.of(food));
        page.setTotal(1);
        when(foodDataMapper.selectPage(any(), any())).thenReturn(page);

        var result = foodService.list("noodle", 1, 10, "rating", "desc");

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).title()).isEqualTo("Noodle shop");
    }

    @Test
    void findByIdThrowsWhenFoodDoesNotExist() {
        when(foodDataMapper.selectById(42)).thenReturn(null);

        assertThatThrownBy(() -> foodService.findById(42))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Food 42 not found");
    }

    @Test
    void createPersistsFoodAndItsItems() {
        when(foodDataMapper.insert(any(FoodData.class))).thenAnswer(invocation -> {
            invocation.<FoodData>getArgument(0).setId(7);
            return 1;
        });
        when(foodDataItemMapper.insert(any(FoodDataItem.class))).thenAnswer(invocation -> {
            invocation.<FoodDataItem>getArgument(0).setId(11);
            return 1;
        });

        FoodResponse response = foodService.create(new FoodCreateRequest(
                "images/thumbnails/a",
                "Rice bowl",
                4.8,
                120,
                new BigDecimal("18.50"),
                30,
                "great",
                List.of(new FoodItemRequest("Beef bowl", "tender", new BigDecimal("16.00")))
        ));

        assertThat(response.id()).isEqualTo(7);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).id()).isEqualTo(11);
        verify(foodDataItemMapper).insert(any(FoodDataItem.class));
    }

    @Test
    void updateThrowsWhenFoodDoesNotExist() {
        when(foodDataMapper.selectById(99)).thenReturn(null);

        assertThatThrownBy(() -> foodService.update(99,
                new FoodUpdateRequest(null, "missing", 4.0, 0, BigDecimal.TEN, 20, null)))
                .isInstanceOf(ApiException.class);

        verify(foodDataMapper, never()).updateById(any(FoodData.class));
    }

    @Test
    void updateItemRejectsItemOwnedByAnotherFood() {
        when(foodDataItemMapper.selectOne(any())).thenReturn(null);

        assertThatThrownBy(() -> foodService.updateItem(1, 2,
                new FoodItemRequest("name", null, BigDecimal.ONE)))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Menu item 2");
    }

    private static FoodData food(int id, String title) {
        FoodData food = new FoodData();
        food.setId(id);
        food.setTitle(title);
        food.setRating(4.5);
        return food;
    }
}
