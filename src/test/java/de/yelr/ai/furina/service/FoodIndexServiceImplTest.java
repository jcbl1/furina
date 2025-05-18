package de.yelr.ai.furina.service;

import com.google.gson.Gson;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import de.yelr.ai.furina.mapper.FoodDataItemMapper;
import de.yelr.ai.furina.mapper.FoodDataMapper;
import de.yelr.ai.furina.service.impl.FoodIndexServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FoodIndexServiceImplTest {

    @Test
    void indexesRestaurantsWithMenuAndMetadata() {
        FoodDataMapper foodDataMapper = mock(FoodDataMapper.class);
        FoodDataItemMapper itemMapper = mock(FoodDataItemMapper.class);
        VectorStore vectorStore = mock(VectorStore.class);

        FoodData food = new FoodData();
        food.setId(3);
        food.setTitle("Noodle shop");
        food.setRating(4.6);
        food.setSalesMonthly(800);
        food.setAverageCost(new BigDecimal("21.50"));
        food.setDeliveryTime(35);
        when(foodDataMapper.selectList(any())).thenReturn(List.of(food));

        FoodDataItem item = new FoodDataItem();
        item.setId(8);
        item.setFoodDataId(3);
        item.setName("Beef noodles");
        item.setPrice(new BigDecimal("13.50"));
        when(itemMapper.selectList(any())).thenReturn(List.of(item));

        FoodIndexServiceImpl service = new FoodIndexServiceImpl(foodDataMapper, itemMapper, vectorStore, new Gson());
        int indexed = service.reindex();

        assertThat(indexed).isEqualTo(1);
        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(captor.capture());
        Document document = captor.getValue().get(0);
        assertThat(document.getId()).isEqualTo("3");
        assertThat(document.getText()).contains("Noodle shop", "Beef noodles");
        assertThat(document.getMetadata()).containsEntry("salesMonthly", 800);
        assertThat(document.getMetadata()).containsEntry("deliveryTime", 35);
    }
}
