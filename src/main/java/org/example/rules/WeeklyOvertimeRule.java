package org.example.rules;

import org.example.model.DailyRecord;
import org.example.model.Wage;
import org.example.util.AttendanceCalculator;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

public class WeeklyOvertimeRule implements PeriodWageRule{

    private final String wageName;
    private final String dailyOvertimeWageName;
    private final Duration weeklyThreshold;

    public WeeklyOvertimeRule(
            String wageName,
            String dailyOvertimeWageName,
            Duration weeklyThreshold
    ) {
        this.wageName = Objects.requireNonNull(wageName);
        this.dailyOvertimeWageName = Objects.requireNonNull(dailyOvertimeWageName);
        this.weeklyThreshold = Objects.requireNonNull(weeklyThreshold);

        if (wageName.isBlank() || dailyOvertimeWageName.isBlank()) {
            throw new IllegalArgumentException(
                    "Wage names cannot be blank"
            );
        }

        if (weeklyThreshold.isNegative()) {
            throw new IllegalArgumentException(
                    "Weekly threshold cannot be negative"
            );
        }
    }


    @Override
    public List<Wage> evaluate(List<DailyRecord> dailyRecords, List<Wage> dailyWages) {
        if (dailyRecords.isEmpty()) {
            return List.of();
        }

        // 1. Calculate actual attendance across the week.
        Duration totalDurationWorkedInWeek = dailyRecords.stream()
                .map(AttendanceCalculator::totalDuration)
                .reduce(Duration.ZERO, Duration::plus);

        // 2. Calculate weekly overtime above the configured threshold.
        Duration weeklyOvertimeDuration =
                totalDurationWorkedInWeek.minus(weeklyThreshold);


        if (weeklyOvertimeDuration.isNegative()) {
            weeklyOvertimeDuration = Duration.ZERO;
        }

        // 3. Sum daily overtime already accounted for.
        Duration dailyOvertimeDuration = dailyWages.stream()
                .filter(wage ->
                        wage.name().equals(dailyOvertimeWageName))
                .map(Wage::duration)
                .reduce(Duration.ZERO, Duration::plus);

        // 4. Reconcile: daily overtime remains unchanged.
        Duration remainingWeeklyOvertime =
                weeklyOvertimeDuration.minus(dailyOvertimeDuration);

        if (remainingWeeklyOvertime.isNegative()) {
            remainingWeeklyOvertime = Duration.ZERO;
        }

        // 5. Associate the weekly wage with Monday.
        LocalDate weekStart = dailyRecords.getFirst().date()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return List.of(
                new Wage(
                        wageName,
                        weekStart,
                        remainingWeeklyOvertime
                )
        );
    }
}
