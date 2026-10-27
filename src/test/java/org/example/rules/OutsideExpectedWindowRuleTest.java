package org.example.rules;

import org.example.model.AttendanceInterval;
import org.example.model.DailyRecord;
import org.example.model.ExpectedWindow;
import org.example.model.Wage;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutsideExpectedWindowRuleTest {

    private final OutsideExpectedWindowRule rule =
            new OutsideExpectedWindowRule("outsideExpectedWindow");

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
    void calculatesTimeBeforeAndAfterExpectedWindow() {
        DailyRecord record = record(
                new AttendanceInterval(
                        LocalTime.of(8, 0), LocalTime.of(18, 0))
        );

        List<Wage> wages = rule.evaluate(record);

        assertEquals(Duration.ofHours(2), wages.getFirst().duration());
    }

    @Test
    void returnsZeroWhenWorkIsInsideExpectedWindow() {
        DailyRecord record = record(
                new AttendanceInterval(
                        LocalTime.of(9, 0), LocalTime.of(17, 0))
        );

        List<Wage> wages = rule.evaluate(record);

        assertEquals(Duration.ZERO, wages.getFirst().duration());
    }

    @Test
    void calculatesOutsideTimeAcrossMultipleIntervals() {
        DailyRecord record = record(
                new AttendanceInterval(
                        LocalTime.of(8, 0), LocalTime.of(10, 0)),
                new AttendanceInterval(
                        LocalTime.of(16, 0), LocalTime.of(18, 0))
        );

        List<Wage> wages = rule.evaluate(record);

        // One hour before 09:00 and one hour after 17:00.
        assertEquals(Duration.ofHours(2), wages.getFirst().duration());
    }

    @Test
    void returnsZeroWhenThereIsNoAttendance() {
        DailyRecord record = record();

        List<Wage> wages = rule.evaluate(record);

        assertEquals(Duration.ZERO, wages.getFirst().duration());
    }
}