package de.yelr.ai.furina.bootstrap;

import de.yelr.ai.furina.service.FoodDataImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.import-seed-data", havingValue = "true")
public class FoodDataImportRunner implements ApplicationRunner {

    private final FoodDataImportService importService;

    public FoodDataImportRunner(FoodDataImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(ApplicationArguments args) {
        int imported = importService.importSeedData();
        log.info("Imported {} food entries from the bundled seed data", imported);
    }
}
