package de.yelr.ai.furina.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import de.yelr.ai.furina.mapper.FoodDataItemMapper;
import de.yelr.ai.furina.mapper.FoodDataMapper;
import de.yelr.ai.furina.service.FoodIndexService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoodIndexServiceImpl implements FoodIndexService {

    private final FoodDataMapper foodDataMapper;
    private final FoodDataItemMapper foodDataItemMapper;
    private final VectorStore vectorStore;
    private final Gson gson;

    public FoodIndexServiceImpl(FoodDataMapper foodDataMapper,
                                FoodDataItemMapper foodDataItemMapper,
                                VectorStore vectorStore,
                                Gson gson) {
        this.foodDataMapper = foodDataMapper;
        this.foodDataItemMapper = foodDataItemMapper;
        this.vectorStore = vectorStore;
        this.gson = gson;
    }

    @Override
    @Transactional(readOnly = true)
    public int reindex() {
        List<FoodData> foods = foodDataMapper.selectList(null);
        if (foods.isEmpty()) {
            return 0;
        }

        List<Integer> foodIds = foods.stream().map(FoodData::getId).toList();
        List<FoodDataItem> items = foodDataItemMapper.selectList(
                Wrappers.<FoodDataItem>lambdaQuery().in(FoodDataItem::getFoodDataId, foodIds));
        Map<Integer, List<FoodDataItem>> itemsByFood = items.stream()
                .collect(Collectors.groupingBy(FoodDataItem::getFoodDataId));

        List<Document> documents = foods.stream()
                .map(food -> toDocument(food, itemsByFood.getOrDefault(food.getId(), List.of())))
                .toList();
        vectorStore.add(documents);
        return documents.size();
    }

    private Document toDocument(FoodData food, List<FoodDataItem> items) {
        FoodSnapshot snapshot = new FoodSnapshot(
                food.getTitle(),
                food.getFeaturedComment(),
                items.stream()
                        .map(item -> new ItemSnapshot(item.getName(), item.getDescription(), item.getPrice()))
                        .toList()
        );
        Document.Builder builder = Document.builder()
                .id(String.valueOf(food.getId()))
                .text(gson.toJson(snapshot))
                .metadata("title", food.getTitle());
        if (food.getRating() != null) {
            builder.metadata("rating", food.getRating());
        }
        if (food.getSalesMonthly() != null) {
            builder.metadata("salesMonthly", food.getSalesMonthly());
        }
        if (food.getAverageCost() != null) {
            builder.metadata("averageCost", food.getAverageCost());
        }
        if (food.getDeliveryTime() != null) {
            builder.metadata("deliveryTime", food.getDeliveryTime());
        }
        return builder.build();
    }

    private record FoodSnapshot(String title, String featuredComment, List<ItemSnapshot> items) {
    }

    private record ItemSnapshot(String name, String description, BigDecimal price) {
    }
}
