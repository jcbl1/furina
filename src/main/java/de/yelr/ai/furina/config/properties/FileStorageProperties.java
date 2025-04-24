package de.yelr.ai.furina.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file-storage")
public record FileStorageProperties(String rootDir) {
}
