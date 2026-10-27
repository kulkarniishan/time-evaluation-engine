package org.example.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public record AttendanceInterval(
        LocalTime start,
        LocalTime end
) {
    public AttendanceInterval {
        Objects.requireNonNull(start, "Start time cannot be null");
        Objects.requireNonNull(end, "End time cannot be null");

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }
    }

    public Duration duration() {
        return Duration.between(start, end);
    }
}
