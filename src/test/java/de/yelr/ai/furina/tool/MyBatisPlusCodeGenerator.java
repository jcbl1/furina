package de.yelr.ai.furina.tool;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * Manual helper for regenerating mappers and entities from a local MySQL
 * instance. Disabled by default because it needs a live database.
 */
class MyBatisPlusCodeGenerator {

    @Test
    @Disabled("Requires a running MySQL instance")
    void generate() {
        FastAutoGenerator.create(
                        "jdbc:mysql://localhost:3306/furina?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8",
                        "root",
                        "my-secret-pw")
                .globalConfig(builder -> builder
                        .author("furina")
                        .outputDir(System.getProperty("user.dir") + "/target/generated-sources/java"))
                .packageConfig(builder -> builder
                        .parent("de.yelr.ai.furina")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                System.getProperty("user.dir") + "/target/generated-sources/java")))
                .strategyConfig(builder -> builder.addTablePrefix("t_"))
                .execute();
    }
}
