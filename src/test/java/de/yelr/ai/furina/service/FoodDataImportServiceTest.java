package de.yelr.ai.furina.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import de.yelr.ai.furina.dto.food.FoodCreateRequest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class FoodDataImportServiceTest {

    @Test
    void importFromMapsSnakeCasePayload() throws Exception {
        FoodService foodService = mock(FoodService.class);
        when(foodService.createBatch(anyList())).thenReturn(1);
        ObjectMapper objectMapper =
                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        FoodDataImportService importService = new FoodDataImportService(foodService, objectMapper);

        String json =
                """
                [
                  {
                    "title": "Noodle shop",
                    "thumbnail_path": "images/thumbnails/noodle",
                    "rating": 4.8,
                    "sales_monthly": 100,
                    "average_cost": 20,
                    "delivery_time": 30,
                    "items": [
                      {"name": "Beef noodles", "price": 12.5}
                    ]
                  }
                ]
                """;

        int imported =
                importService.importFrom(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        assertThat(imported).isEqualTo(1);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<java.util.List<FoodCreateRequest>> captor =
                ArgumentCaptor.forClass(java.util.List.class);
        verify(foodService).createBatch(captor.capture());
        FoodCreateRequest request = captor.getValue().get(0);
        assertThat(request.title()).isEqualTo("Noodle shop");
        assertThat(request.thumbnailPath()).isEqualTo("images/thumbnails/noodle");
        assertThat(request.items()).hasSize(1);
        assertThat(request.items().get(0).price()).isEqualByComparingTo("12.5");
    }

    @Test
    void bundledSnapshotIsReadable() {
        FoodService foodService = mock(FoodService.class);
        when(foodService.createBatch(anyList())).thenReturn(1);
        ObjectMapper objectMapper =
                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        FoodDataImportService importService = new FoodDataImportService(foodService, objectMapper);

        importService.importSeedData();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<java.util.List<FoodCreateRequest>> captor =
                ArgumentCaptor.forClass(java.util.List.class);
        verify(foodService).createBatch(captor.capture());
        assertThat(captor.getValue()).isNotEmpty();
        assertThat(captor.getValue().get(0).title()).isNotBlank();
    }
}
