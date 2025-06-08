package de.yelr.ai.furina.service;

import java.io.IOException;
import java.io.InputStream;

public interface FoodDataImportService {

    int importSeedData();

    int importFrom(InputStream input) throws IOException;
}
