
package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;

public class AttendanceInputLoader {

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    public AttendanceInput load() throws IOException {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("attendance.json")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Could not find attendance.json in resources"
                );
            }

            return objectMapper.readValue(
                    input,
                    AttendanceInput.class
            );
        }
    }
}
