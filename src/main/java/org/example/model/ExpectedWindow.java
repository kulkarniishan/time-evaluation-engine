package org.example.model;

import java.time.LocalTime;
import java.util.Objects;

public record ExpectedWindow(
        LocalTime start,
        LocalTime end
) {
    public ExpectedWindow {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Window end must be after start"
            );
        }
    }
}