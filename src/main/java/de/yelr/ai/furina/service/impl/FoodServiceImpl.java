package de.yelr.ai.furina.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.dto.food.FoodSummaryResponse;
import de.yelr.ai.furina.entity.FoodData;
import de.yelr.ai.furina.entity.FoodDataItem;
import de.yelr.ai.furina.mapper.FoodDataItemMapper;
import de.yelr.ai.furina.mapper.FoodDataMapper;
import de.yelr.ai.furina.service.FoodService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    private static void applySort(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FoodData> query,
                                  String sortBy,
                                  String direction) {
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
