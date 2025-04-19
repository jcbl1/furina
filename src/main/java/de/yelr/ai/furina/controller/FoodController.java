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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return ApiResponse.success(foodService.list(keyword, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResponse>> detail(@PathVariable int id) {
        FoodResponse food = foodService.findById(id);
        return food == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(ApiResponse.success(food));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodResponse>> create(@RequestBody FoodCreateRequest request) {
        FoodResponse created = foodService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<FoodBatchCreateResponse>> createBatch(
            @RequestBody List<FoodCreateRequest> requests) {
        int inserted = foodService.createBatch(requests);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(new FoodBatchCreateResponse(inserted)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResponse>> update(
            @PathVariable int id,
            @RequestBody FoodUpdateRequest request) {
        FoodResponse updated = foodService.update(id, request);
        return updated == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        return foodService.delete(id)
                ? ResponseEntity.ok(ApiResponse.success())
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ApiResponse<FoodItemResponse>> addItem(
            @PathVariable int id,
            @RequestBody FoodItemRequest request) {
        FoodItemResponse item = foodService.addItem(id, request);
        return item == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(item));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<ApiResponse<FoodItemResponse>> updateItem(
            @PathVariable int id,
            @PathVariable int itemId,
            @RequestBody FoodItemRequest request) {
        FoodItemResponse item = foodService.updateItem(id, itemId, request);
        return item == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(ApiResponse.success(item));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable int id, @PathVariable int itemId) {
        return foodService.deleteItem(id, itemId)
                ? ResponseEntity.ok(ApiResponse.success())
                : ResponseEntity.notFound().build();
    }
}
