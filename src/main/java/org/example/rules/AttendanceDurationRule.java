package org.example.rules;

import org.example.model.DailyRecord;
import org.example.model.Wage;
import org.example.util.AttendanceCalculator;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class AttendanceDurationRule implements WageRule {
    private final String wageName;

    public AttendanceDurationRule(String wageName) {
        this.wageName = Objects.requireNonNull(wageName);

        if (wageName.isBlank()) {
            throw new IllegalArgumentException(
                    "Wage name cannot be blank"
            );
        }
    }

    @Override
    public List<Wage> evaluate(
            DailyRecord dailyRecord
    ) {

        Duration totalDuration = AttendanceCalculator.totalDuration(dailyRecord);

        return List.of(
                new Wage(
                        wageName,
                        dailyRecord.date(),
                        totalDuration
                )
        );
    }
}