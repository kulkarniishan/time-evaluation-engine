package org.example.rules;

import org.example.model.AttendanceInterval;
import org.example.model.DailyRecord;
import org.example.model.ExpectedWindow;
import org.example.model.Wage;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SundayAndPublicHolidayRuleTest {

    private final SundayAndPublicHolidayRule rule =
            new SundayAndPublicHolidayRule(
                    "sundayWork", "publicHolidayWork");

    private DailyRecord record(
            LocalDate date, int hours, boolean publicHoliday) {

        return new DailyRecord(
                date,
                hours == 0 ? List.of() : List.of(
                        new AttendanceInterval(
                                LocalTime.of(9, 0),
                                LocalTime.of(9, 0).plusHours(hours)
                        )
                ),
                publicHoliday,
                Duration.ofHours(8),
                new ExpectedWindow(
                        LocalTime.of(9, 0),
                        LocalTime.of(17, 0)
                )
        );
    }

    @Test
    void calculatesSundayWork() {
        LocalDate sunday = LocalDate.of(2026, 11, 1);

        List<Wage> wages = rule.evaluate(record(sunday, 5, false));

        assertEquals(1, wages.size());
        assertEquals("sundayWork", wages.getFirst().name());
        assertEquals(Duration.ofHours(5), wages.getFirst().duration());
    }

    @Test
    void publicHolidayTakesPrecedenceOverSunday() {
        LocalDate sunday = LocalDate.of(2026, 11, 1);

        List<Wage> wages = rule.evaluate(record(sunday, 5, true));

        assertEquals(1, wages.size());
        assertEquals("publicHolidayWork", wages.getFirst().name());
        assertEquals(Duration.ofHours(5), wages.getFirst().duration());
    }

    @Test
    void calculatesPublicHolidayWorkOnAWeekday() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        List<Wage> wages = rule.evaluate(record(monday, 5, true));

        assertEquals(1, wages.size());
        assertEquals("publicHolidayWork", wages.getFirst().name());
        assertEquals(Duration.ofHours(5), wages.getFirst().duration());
    }

    @Test
    void returnsNoWageForAnOrdinaryWeekday() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        List<Wage> wages = rule.evaluate(record(monday, 5, false));

        assertTrue(wages.isEmpty());
    }

    @Test
    void returnsNoWageWhenThereIsNoAttendance() {
        LocalDate sunday = LocalDate.of(2026, 11, 1);

        List<Wage> wages = rule.evaluate(record(sunday, 0, false));

        System.out.println(wages);
        assertTrue(wages.isEmpty());
    }
}