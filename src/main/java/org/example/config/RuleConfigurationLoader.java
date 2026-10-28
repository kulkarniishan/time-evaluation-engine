package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class RuleConfigurationLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RuleConfiguration load() throws IOException {
        return load(null);
    }

    public RuleConfiguration load(String path) throws IOException {
        if (path != null && !path.isBlank()) {
            return objectMapper.readValue(
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

            return objectMapper.readValue(
                    input,
                    RuleConfiguration.class
            );
        }
    }
}
