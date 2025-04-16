package de.yelr.ai.furina.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.food.FoodCreateRequest;
import de.yelr.ai.furina.dto.food.FoodItemRequest;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.dto.food.FoodSummaryResponse;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import de.yelr.ai.furina.mapper.FoodDataItemMapper;
import de.yelr.ai.furina.mapper.FoodDataMapper;
import de.yelr.ai.furina.service.FoodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    private static final int MAX_PAGE_SIZE = 100;

    private final FoodDataMapper foodDataMapper;
    private final FoodDataItemMapper foodDataItemMapper;

    public FoodServiceImpl(FoodDataMapper foodDataMapper, FoodDataItemMapper foodDataItemMapper) {
        this.foodDataMapper = foodDataMapper;
        this.foodDataItemMapper = foodDataItemMapper;
    }

    @Override
    public PageResponse<FoodSummaryResponse> list(String keyword, int page, int size, String sortBy, String direction) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        var query = Wrappers.<FoodData>lambdaQuery();
        if (StringUtils.hasText(keyword)) {
            query.and(wrapper -> wrapper
                    .like(FoodData::getTitle, keyword.trim())
                    .or()
                    .like(FoodData::getFeaturedComment, keyword.trim()));
        }
        applySort(query, sortBy, direction);

        Page<FoodData> result = foodDataMapper.selectPage(new Page<>(safePage, safeSize), query);
        List<FoodSummaryResponse> items = result.getRecords().stream()
                .map(FoodSummaryResponse::from)
                .toList();

        return PageResponse.of(items, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public FoodResponse findById(int id) {
        FoodData food = foodDataMapper.selectById(id);
        if (food == null) {
            return null;
        }
        List<FoodDataItem> items = foodDataItemMapper.selectList(
                Wrappers.<FoodDataItem>lambdaQuery().eq(FoodDataItem::getFoodDataId, id));
        return FoodResponse.from(food, items);
    }

    @Override
    @Transactional
    public FoodResponse create(FoodCreateRequest request) {
        FoodData food = new FoodData();
        food.setThumbnailPath(request.thumbnailPath());
        food.setTitle(request.title());
        food.setRating(request.rating());
        food.setSalesMonthly(request.salesMonthly());
        food.setAverageCost(request.averageCost());
        food.setDeliveryTime(request.deliveryTime());
        food.setFeaturedComment(request.featuredComment());
        foodDataMapper.insert(food);

        List<FoodDataItem> items = insertItems(food.getId(), request.items());
        return FoodResponse.from(food, items);
    }

    @Override
    @Transactional
    public int createBatch(List<FoodCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return 0;
        }
        int inserted = 0;
        for (FoodCreateRequest request : requests) {
            create(request);
            inserted++;
        }
        return inserted;
    }

    private List<FoodDataItem> insertItems(Integer foodId, List<FoodItemRequest> itemRequests) {
        List<FoodDataItem> items = new ArrayList<>();
        if (itemRequests == null) {
            return items;
        }
        for (FoodItemRequest itemRequest : itemRequests) {
            FoodDataItem item = new FoodDataItem();
            item.setFoodDataId(foodId);
            item.setName(itemRequest.name());
            item.setDescription(itemRequest.description());
            item.setPrice(itemRequest.price());
            foodDataItemMapper.insert(item);
            items.add(item);
        }
        return items;
    }

    private static void applySort(LambdaQueryWrapper<FoodData> query, String sortBy, String direction) {
        boolean ascending = "asc".equalsIgnoreCase(direction);
        switch (sortBy == null ? "id" : sortBy) {
            case "title" -> query.orderBy(true, ascending, FoodData::getTitle);
            case "rating" -> query.orderBy(true, ascending, FoodData::getRating);
            case "salesMonthly" -> query.orderBy(true, ascending, FoodData::getSalesMonthly);
            case "averageCost" -> query.orderBy(true, ascending, FoodData::getAverageCost);
            case "deliveryTime" -> query.orderBy(true, ascending, FoodData::getDeliveryTime);
            default -> query.orderBy(true, ascending, FoodData::getId);
        }
    }
}
