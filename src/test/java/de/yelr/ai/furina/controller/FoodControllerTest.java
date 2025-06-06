package de.yelr.ai.furina.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import de.yelr.ai.furina.dto.food.FoodItemResponse;
import de.yelr.ai.furina.dto.food.FoodResponse;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.exception.GlobalExceptionHandler;
import de.yelr.ai.furina.service.FoodService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class FoodControllerTest {

    @Mock private FoodService foodService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper =
                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc =
                MockMvcBuilders.standaloneSetup(new FoodController(foodService))
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                        .setValidator(validator)
                        .build();
    }

    @Test
    void returnsFoodDetail() throws Exception {
        when(foodService.findById(3))
                .thenReturn(
                        new FoodResponse(
                                3,
                                "images/thumbnails/3",
                                "Noodle shop",
                                4.8,
                                800,
                                new BigDecimal("20.00"),
                                35,
                                "great",
                                List.of(
                                        new FoodItemResponse(
                                                10,
                                                "Beef noodles",
                                                null,
                                                new BigDecimal("12.50")))));

        mockMvc.perform(get("/api/v1/foods/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("Noodle shop"))
                .andExpect(jsonPath("$.data.average_cost").value(20.00))
                .andExpect(jsonPath("$.data.items[0].name").value("Beef noodles"));
    }

    @Test
    void mapsNotFoundToApiError() throws Exception {
        when(foodService.findById(9)).thenThrow(ApiException.notFound("Food 9 not found"));

        mockMvc.perform(get("/api/v1/foods/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Food 9 not found"));
    }

    @Test
    void rejectsInvalidCreatePayload() throws Exception {
        mockMvc.perform(
                        post("/api/v1/foods")
                                .contentType("application/json")
                                .content("{\"rating\": 4.8}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.title").value("title is required"));
    }
}
