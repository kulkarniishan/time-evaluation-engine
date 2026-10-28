
package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.util.JsonMappers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class AttendanceInputLoader {

    public AttendanceInput load() throws IOException {
        return load(null);
    }

    public AttendanceInput load(String path) throws IOException {
        if (path != null && !path.isBlank()) {
            return JsonMappers.DEFAULT.readValue(
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

            return JsonMappers.DEFAULT.readValue(
                    input,
                    AttendanceInput.class
            );
        }
    }
}
