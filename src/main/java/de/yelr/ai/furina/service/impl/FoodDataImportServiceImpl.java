package de.yelr.ai.furina.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.yelr.ai.furina.dto.food.FoodCreateRequest;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.service.FoodDataImportService;
import de.yelr.ai.furina.service.FoodService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * Imports the bundled catalog snapshot. The snapshot is intentionally kept outside the application
 * startup path unless explicitly enabled.
 */
@Service
public class FoodDataImportServiceImpl implements FoodDataImportService {

    private static final String SEED_RESOURCE = "database-json/food_data.json";

    private final FoodService foodService;
    private final ObjectMapper objectMapper;

    public FoodDataImportServiceImpl(FoodService foodService, ObjectMapper objectMapper) {
        this.foodService = foodService;
        this.objectMapper = objectMapper;
    }

    @Override
    public int importSeedData() {
        try (InputStream input = new ClassPathResource(SEED_RESOURCE).getInputStream()) {
            return importFrom(input);
        } catch (IOException exception) {
            throw ApiException.internal("Unable to read seed data: " + exception.getMessage());
        }
    }

    @Override
    public int importFrom(InputStream input) throws IOException {
        List<FoodCreateRequest> requests = objectMapper.readValue(input, new TypeReference<>() {});
        return foodService.createBatch(requests);
    }
}
