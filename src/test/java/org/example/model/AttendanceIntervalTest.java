package org.example.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceIntervalTest {

    @Test
    void calculatesDurationCorrectly() {
        var interval = new AttendanceInterval(
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        assertEquals(Duration.ofHours(3), interval.duration());
    }

    @Test
    void rejectsInvalidInterval() {
        assertThrows(IllegalArgumentException.class, () ->
                new AttendanceInterval(
                        LocalTime.of(14, 0),
                        LocalTime.of(12, 0)
                )
        );
    }
}