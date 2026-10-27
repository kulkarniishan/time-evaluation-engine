package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class RuleConfigurationLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RuleConfiguration load() throws IOException {
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
