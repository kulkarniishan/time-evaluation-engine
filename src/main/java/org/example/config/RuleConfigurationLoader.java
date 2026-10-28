package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.util.JsonMappers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class RuleConfigurationLoader {

    public RuleConfiguration load() throws IOException {
        return load(null);
    }

    public RuleConfiguration load(String path) throws IOException {
        if (path != null && !path.isBlank()) {
            return JsonMappers.DEFAULT.readValue(
                    Path.of(path).toFile(),
                    RuleConfiguration.class
            );
        }

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("rules.json")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Could not find rules.json in src/main/resources"
                );
            }

            return JsonMappers.DEFAULT.readValue(
                    input,
                    RuleConfiguration.class
            );
        }
    }
}
