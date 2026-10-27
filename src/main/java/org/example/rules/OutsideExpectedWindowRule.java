package org.example.rules;

import org.example.model.DailyRecord;
import org.example.model.Wage;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class OutsideExpectedWindowRule implements WageRule{

    private final String  wageName;

    public OutsideExpectedWindowRule(String wageName) {
        this.wageName = Objects.requireNonNull(wageName);

        if (wageName.isBlank()) {
            throw new IllegalArgumentException(
                    "Wage name cannot be blank"
            );

        }
    }
    @Override
    public List<Wage> evaluate(DailyRecord dailyRecord) {
        LocalTime dailyStart = dailyRecord.expectedWindow().start();
        LocalTime dailyEnd = dailyRecord.expectedWindow().end();

        Duration durationOutsideExpectedWindow = dailyRecord.intervals().stream()
                .map(interval -> {
                    Duration totalDuration = interval.duration();

                    LocalTime overlapStart = interval.start().isAfter(dailyStart)
                            ? interval.start()
                            : dailyStart;

                    LocalTime overlapEnd = interval.end().isBefore(dailyEnd)
                            ? interval.end()
                            : dailyEnd;

                    Duration overlapDuration = overlapStart.isBefore(overlapEnd)
                            ? Duration.between(overlapStart, overlapEnd)
                            : Duration.ZERO;

                    return totalDuration.minus(overlapDuration);
                })
                .reduce(Duration.ZERO, Duration::plus);

        return List.of(new Wage(
                wageName,
                dailyRecord.date(),
                durationOutsideExpectedWindow
                ));
    }
}
