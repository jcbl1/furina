package de.yelr.ai.furina;

import org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication(exclude = OllamaAutoConfiguration.class)
public class FurinaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FurinaApplication.class, args);
    }
}
