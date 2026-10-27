package org.example.rules;

import org.example.model.AttendanceInterval;
import org.example.model.DailyRecord;
import org.example.model.ExpectedWindow;
import org.example.model.Wage;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceDurationRuleTest {

    private final AttendanceDurationRule rule =
            new AttendanceDurationRule("attendanceDuration");

    private final LocalDate date = LocalDate.of(2026, 10, 26);

    private DailyRecord record(AttendanceInterval... intervals) {
        return new DailyRecord(
                date,
                List.of(intervals),
                false,
                Duration.ofHours(8),
                new ExpectedWindow(
                        LocalTime.of(9, 0),
                        LocalTime.of(17, 0)
                )
        );
    }

    @Test
    void calculatesDurationOfOneInterval() {
        DailyRecord record = record(
                new AttendanceInterval(
                        LocalTime.of(8, 0),
                        LocalTime.of(18, 0)
                )
        );

        List<Wage> wages = rule.evaluate(record);

        assertEquals(1, wages.size());
        assertEquals(Duration.ofHours(10), wages.getFirst().duration());
    }

    @Test
    void sumsIntervalsWithoutCountingGaps() {
        DailyRecord record = record(
                new AttendanceInterval(
                        LocalTime.of(9, 0),
                        LocalTime.of(12, 0)
                ),
                new AttendanceInterval(
                        LocalTime.of(13, 0),
                        LocalTime.of(17, 0)
                )
        );

        List<Wage> wages = rule.evaluate(record);

        assertEquals(Duration.ofHours(7), wages.getFirst().duration());
    }

    @Test
    void returnsZeroForNoIntervals() {
        DailyRecord record = record();

        List<Wage> wages = rule.evaluate(record);

        assertEquals(Duration.ZERO, wages.getFirst().duration());
    }
}