
package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class AttendanceInputLoader {

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    public AttendanceInput load() throws IOException {
        return load(null);
    }

    public AttendanceInput load(String path) throws IOException {
        if (path != null && !path.isBlank()) {
            return objectMapper.readValue(
                    Path.of(path).toFile(),
                    AttendanceInput.class
            );
        }

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("attendance.json")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Could not find test-attendance.json in resources"
                );
            }

            return objectMapper.readValue(
                    input,
                    AttendanceInput.class
            );
        }
    }
}
