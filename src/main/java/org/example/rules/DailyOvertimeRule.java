package org.example.rules;

import org.example.model.DailyRecord;
import org.example.model.Wage;
import org.example.util.AttendanceCalculator;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DailyOvertimeRule implements WageRule {

    private final Set<DayOfWeek> applicableDays;
    private final String wageName;

    public DailyOvertimeRule(Set<DayOfWeek> applicableDays, String wageName) {
        this.applicableDays = Set.copyOf(
                Objects.requireNonNull(applicableDays)
        );

        this.wageName = Objects.requireNonNull(wageName);

        if (wageName.isBlank()) {
            throw new IllegalArgumentException(
                    "Wage name cannot be blank"
            );
        }

        if (applicableDays.isEmpty()) {
            throw new IllegalArgumentException(
                    "applicableDays cannot be blank"
            );
        }
    }

    @Override
    public List<Wage> evaluate(DailyRecord dailyRecord) {
        if (!applicableDays.contains(dailyRecord.getDayOfWeek())) {
            return List.of();
        }

        Duration workedDuration = AttendanceCalculator.totalDuration(dailyRecord);
        Duration overtimeDuration = Duration.ZERO;

        if (workedDuration.compareTo(dailyRecord.expectedDuration()) > 0) {
            overtimeDuration = workedDuration.minus(dailyRecord.expectedDuration());
        }

        return List.of(new Wage(
                wageName,
                dailyRecord.date(),
                overtimeDuration
        ));
    }
}
