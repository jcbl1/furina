package de.yelr.ai.furina.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.yelr.ai.furina.config.properties.FileStorageProperties;
import de.yelr.ai.furina.exception.GlobalExceptionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FileControllerTest {

    @TempDir Path root;

    @Test
    void servesExistingThumbnail() throws Exception {
        Path directory = Files.createDirectories(root.resolve("images/thumbnails"));
        byte[] content = "image-bytes".getBytes(StandardCharsets.UTF_8);
        Files.write(directory.resolve("food.jpg"), content);

        MockMvc mockMvc = mockMvc(root);

        mockMvc.perform(get("/api/v1/files/thumbnails/food.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("max-age=86400")))
                .andExpect(content().bytes(content));
    }

    @Test
    void returnsNotFoundForMissingThumbnail() throws Exception {
        MockMvc mockMvc = mockMvc(root);

        mockMvc.perform(get("/api/v1/files/thumbnails/missing.jpg"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"code\":404}"));
    }

    private static MockMvc mockMvc(Path root) {
        FileController controller = new FileController(new FileStorageProperties(root.toString()));
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
}
