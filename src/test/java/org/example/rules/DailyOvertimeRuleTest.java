package org.example.rules;

import org.example.model.AttendanceInterval;
import org.example.model.DailyRecord;
import org.example.model.ExpectedWindow;
import org.example.model.Wage;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DailyOvertimeRuleTest {

    private final DailyOvertimeRule rule =
            new DailyOvertimeRule(
                    Set.of(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY
                    ),
                    "dailyOvertime"
            );

    private DailyRecord record(LocalDate date, int hours) {
        List<AttendanceInterval> intervals =
                hours == 0 ? List.of() : List.of(
                        new AttendanceInterval(
                                LocalTime.of(8, 0),
                                LocalTime.of(8, 0).plusHours(hours)
                        )
                );

        return new DailyRecord(
                date,
                intervals,
                false,
                Duration.ofHours(8),
                new ExpectedWindow(
                        LocalTime.of(9, 0),
                        LocalTime.of(17, 0)
                )
        );
    }

    @Test
    void calculatesOvertimeWhenWorkExceedsExpectedDuration() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        List<Wage> wages = rule.evaluate(record(monday, 10));

        assertEquals(Duration.ofHours(2), wages.getFirst().duration());
    }

    @Test
    void returnsZeroWhenWorkEqualsExpectedDuration() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        List<Wage> wages = rule.evaluate(record(monday, 8));

        assertEquals(Duration.ZERO, wages.getFirst().duration());
    }

    @Test
    void returnsZeroWhenWorkIsBelowExpectedDuration() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        List<Wage> wages = rule.evaluate(record(monday, 6));

        assertEquals(Duration.ZERO, wages.getFirst().duration());
    }

    @Test
    void doesNotCalculateOvertimeOnNonApplicableDay() {
        LocalDate saturday = LocalDate.of(2026, 10, 31);

        List<Wage> wages = rule.evaluate(record(saturday, 10));

        assertEquals(Duration.ZERO, wages.getFirst().duration());
    }

    @Test
    void returnsZeroWhenThereIsNoAttendance() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        List<Wage> wages = rule.evaluate(record(monday, 0));

        assertEquals(Duration.ZERO, wages.getFirst().duration());
    }
}