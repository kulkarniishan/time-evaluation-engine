package org.example.engine;

import org.example.config.DailyRuleConfig;
import org.example.config.PeriodRuleConfig;
import org.example.config.RuleConfiguration;
import org.example.model.AttendanceInterval;
import org.example.model.DailyRecord;
import org.example.model.ExpectedWindow;
import org.example.model.Wage;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeEvaluationEngineTest {

    private final LocalDate monday = LocalDate.of(2026, 10, 26);

    private DailyRecord record(LocalDate date, int hours) {
        List<AttendanceInterval> intervals = hours == 0
                ? List.of()
                : List.of(new AttendanceInterval(
                LocalTime.of(8, 0),
                LocalTime.of(8, 0).plusHours(hours)
        ));

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

    private RuleConfiguration configuration() {
        return new RuleConfiguration(
                List.of(
                        new DailyRuleConfig(
                                "attendanceDuration",
                                "attendanceDuration",
                                List.of(),
                                null,
                                null,
                                true
                        ),
                        new DailyRuleConfig(
                                "dailyOvertime",
                                "dailyOvertime",
                                List.of(
                                        DayOfWeek.MONDAY,
                                        DayOfWeek.TUESDAY,
                                        DayOfWeek.WEDNESDAY,
                                        DayOfWeek.THURSDAY,
                                        DayOfWeek.FRIDAY
                                ),
                                null,
                                null,
                                true
                        )
                ),
                List.of(
                        new PeriodRuleConfig(
                                "weeklyOvertime",
                                "weeklyOvertime",
                                "dailyOvertime",
                                Duration.ofHours(40),
                                true
                        )
                )
        );
    }

    @Test
    void evaluatesDailyAndWeeklyRulesTogether() {
        int[] hours = {10, 10, 8, 8, 6, 0, 0};

        List<DailyRecord> records = new ArrayList<>();

        for (int i = 0; i < hours.length; i++) {
            records.add(record(monday.plusDays(i), hours[i]));
        }

        TimeEvaluationEngine engine =
                new TimeEvaluationEngine(configuration());

        List<Wage> wages = engine.evaluate(records);

        Duration totalAttendance = wages.stream()
                .filter(w -> w.name().equals("attendanceDuration"))
                .map(Wage::duration)
                .reduce(Duration.ZERO, Duration::plus);

        Duration totalDailyOvertime = wages.stream()
                .filter(w -> w.name().equals("dailyOvertime"))
                .map(Wage::duration)
                .reduce(Duration.ZERO, Duration::plus);

        Wage weeklyOvertime = wages.stream()
                .filter(w -> w.name().equals("weeklyOvertime"))
                .findFirst()
                .orElseThrow();

        assertEquals(Duration.ofHours(42), totalAttendance);
        assertEquals(Duration.ofHours(4), totalDailyOvertime);
        assertEquals(Duration.ZERO, weeklyOvertime.duration());
        assertEquals(monday, weeklyOvertime.date());
    }

    @Test
    void calculatesRemainingWeeklyOvertimeCorrectly() {
        int[] hours = {9, 9, 8, 8, 8, 4, 0};

        List<DailyRecord> records = new ArrayList<>();

        for (int i = 0; i < hours.length; i++) {
            records.add(record(monday.plusDays(i), hours[i]));
        }

        TimeEvaluationEngine engine =
                new TimeEvaluationEngine(configuration());

        List<Wage> wages = engine.evaluate(records);

        Wage weeklyOvertime = wages.stream()
                .filter(w -> w.name().equals("weeklyOvertime"))
                .findFirst()
                .orElseThrow();

        assertEquals(Duration.ofHours(46),
                records.stream()
                        .flatMap(r -> r.intervals().stream())
                        .map(AttendanceInterval::duration)
                        .reduce(Duration.ZERO, Duration::plus));

        assertEquals(Duration.ofHours(2), wages.stream()
                .filter(w -> w.name().equals("dailyOvertime"))
                .map(Wage::duration)
                .reduce(Duration.ZERO, Duration::plus));

        assertEquals(Duration.ofHours(4), weeklyOvertime.duration());
    }

    @Test
    void rejectsDuplicateDailyRecordDates() {
        List<DailyRecord> records = List.of(
                record(monday, 8),
                record(monday, 6)
        );

        TimeEvaluationEngine engine =
                new TimeEvaluationEngine(configuration());

        assertThrows(IllegalArgumentException.class,
                () -> engine.evaluate(records));
    }

    @Test
    void disabledAttendanceRuleProducesNoAttendanceWages() {
        RuleConfiguration disabledConfiguration = new RuleConfiguration(
                List.of(
                        new DailyRuleConfig(
                                "attendanceDuration",
                                "attendanceDuration",
                                List.of(),
                                null,
                                null,
                                false
                        )
                ),
                List.of()
        );

        TimeEvaluationEngine engine =
                new TimeEvaluationEngine(disabledConfiguration);

        List<Wage> wages = engine.evaluate(
                List.of(record(monday, 8))
        );

        assertTrue(wages.stream().noneMatch(
                wage -> wage.name().equals("attendanceDuration")
        ));
    }
}