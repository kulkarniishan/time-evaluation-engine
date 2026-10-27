package org.example.model;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public record DailyRecord(
        LocalDate date,
        List<AttendanceInterval>intervals,
        boolean publicHoliday,
        Duration expectedDuration,
        ExpectedWindow expectedWindow
) {
    public DailyRecord {
        Objects.requireNonNull(date);
        Objects.requireNonNull(expectedDuration);
        Objects.requireNonNull(expectedWindow);
        intervals = List.copyOf(
                Objects.requireNonNull(intervals)
        );

        if (expectedDuration.isNegative()) {
            throw new IllegalArgumentException(
                    "Expected duration cannot be negative"
            );
        }
    }

    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public Boolean isSunday() {
        return getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }
}