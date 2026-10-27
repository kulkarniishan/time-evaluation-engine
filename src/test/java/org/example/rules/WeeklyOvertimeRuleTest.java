package org.example.rules;

import org.example.model.AttendanceInterval;
import org.example.model.DailyRecord;
import org.example.model.ExpectedWindow;
import org.example.model.Wage;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyOvertimeRuleTest {

    private final WeeklyOvertimeRule rule =
            new WeeklyOvertimeRule(
                    "weeklyOvertime",
                    "dailyOvertime",
                    Duration.ofHours(40)
            );

    private DailyRecord day(LocalDate date, int hours) {
        return new DailyRecord(
                date,
                hours == 0 ? List.of() : List.of(
                        new AttendanceInterval(
                                LocalTime.MIDNIGHT,
                                LocalTime.MIDNIGHT.plusHours(hours)
                        )
                ),
                false,
                Duration.ofHours(8),
                new ExpectedWindow(
                        LocalTime.of(9, 0),
                        LocalTime.of(17, 0)
                )
        );
    }

    @Test
    void firstAssignmentExampleHasNoRemainingWeeklyOvertime() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        int[] hours = {10, 10, 8, 8, 6, 0, 0};
        List<DailyRecord> days = new ArrayList<>();

        for (int i = 0; i < hours.length; i++) {
            days.add(day(monday.plusDays(i), hours[i]));
        }

        List<Wage> dailyWages = List.of(
                new Wage("dailyOvertime", monday, Duration.ofHours(2)),
                new Wage("dailyOvertime", monday.plusDays(1),
                        Duration.ofHours(2))
        );

        List<Wage> result = rule.evaluate(days, dailyWages);

        assertEquals(1, result.size());
        assertEquals(Duration.ZERO, result.getFirst().duration());
        assertEquals(monday, result.getFirst().date());
    }

    @Test
    void secondAssignmentExampleHasFourHoursRemaining() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        int[] hours = {9, 9, 8, 8, 8, 4, 0};
        List<DailyRecord> days = new ArrayList<>();

        for (int i = 0; i < hours.length; i++) {
            days.add(day(monday.plusDays(i), hours[i]));
        }

        List<Wage> dailyWages = List.of(
                new Wage("dailyOvertime", monday, Duration.ofHours(1)),
                new Wage("dailyOvertime", monday.plusDays(1),
                        Duration.ofHours(1))
        );

        List<Wage> result = rule.evaluate(days, dailyWages);

        assertEquals(Duration.ofHours(4), result.getFirst().duration());
    }

    @Test
    void returnsZeroAtExactlyFortyHours() {
        LocalDate monday = LocalDate.of(2026, 10, 26);

        int[] hours = {8, 8, 8, 8, 8, 0, 0};
        List<DailyRecord> days = new ArrayList<>();

        for (int i = 0; i < hours.length; i++) {
            days.add(day(monday.plusDays(i), hours[i]));
        }

        List<Wage> result = rule.evaluate(days, List.of());

        assertEquals(Duration.ZERO, result.getFirst().duration());
    }

    @Test
    void returnsNoResultForEmptyAttendance() {
        List<Wage> result = rule.evaluate(List.of(), List.of());

        assertTrue(result.isEmpty());
    }
}
