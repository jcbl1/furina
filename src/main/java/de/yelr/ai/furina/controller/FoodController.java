package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.common.ApiResponse;
import de.yelr.ai.furina.common.PageResponse;
import de.yelr.ai.furina.dto.food.FoodBatchCreateResponse;
import de.yelr.ai.furina.dto.food.FoodCreateRequest;
import de.yelr.ai.furina.dto.food.FoodItemRequest;
import de.yelr.ai.furina.dto.food.FoodItemResponse;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.dto.food.FoodSummaryResponse;
import de.yelr.ai.furina.dto.food.FoodUpdateRequest;
import de.yelr.ai.furina.service.FoodService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ApiResponse<PageResponse<FoodSummaryResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return ApiResponse.success(foodService.list(keyword, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public ApiResponse<FoodResponse> detail(@PathVariable int id) {
        return ApiResponse.success(foodService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodResponse>> create(
            @Valid @RequestBody FoodCreateRequest request) {
        FoodResponse created = foodService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<FoodBatchCreateResponse>> createBatch(
            @RequestBody List<@Valid FoodCreateRequest> requests) {
        int inserted = foodService.createBatch(requests);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(new FoodBatchCreateResponse(inserted)));
    }

    @PutMapping("/{id}")
    public ApiResponse<FoodResponse> update(
            @PathVariable int id, @Valid @RequestBody FoodUpdateRequest request) {
        return ApiResponse.success(foodService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) {
        foodService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ApiResponse<FoodItemResponse>> addItem(
            @PathVariable int id, @Valid @RequestBody FoodItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(foodService.addItem(id, request)));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ApiResponse<FoodItemResponse> updateItem(
            @PathVariable int id,
            @PathVariable int itemId,
            @Valid @RequestBody FoodItemRequest request) {
        return ApiResponse.success(foodService.updateItem(id, itemId, request));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ApiResponse<Void> deleteItem(@PathVariable int id, @PathVariable int itemId) {
        foodService.deleteItem(id, itemId);
        return ApiResponse.success();
    }
}
