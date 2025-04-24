package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.config.properties.FileStorageProperties;
import de.yelr.ai.furina.exception.ApiException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final Path rootDirectory;

    public FileController(FileStorageProperties properties) {
        this.rootDirectory = Paths.get(properties.rootDir()).toAbsolutePath().normalize();
    }

    @GetMapping("/thumbnails/{fileName}")
    public ResponseEntity<Resource> thumbnail(@PathVariable String fileName) throws IOException {
        Path thumbnailDirectory = rootDirectory.resolve("images/thumbnails").normalize();
        Path file = thumbnailDirectory.resolve(fileName).normalize();

        if (!file.startsWith(thumbnailDirectory) || !Files.isRegularFile(file)) {
            throw ApiException.notFound("Thumbnail not found");
        }

        MediaType mediaType = detectMediaType(file);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(new FileSystemResource(file));
    }

    private static MediaType detectMediaType(Path file) throws IOException {
        String contentType = Files.probeContentType(file);
        if (!StringUtils.hasText(contentType)) {
            return MediaType.IMAGE_JPEG;
        }
        try {
            return MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
