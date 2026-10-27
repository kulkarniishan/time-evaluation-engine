package org.example.rules;

import org.example.model.DailyRecord;
import org.example.model.Wage;
import org.example.util.AttendanceCalculator;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class SundayAndPublicHolidayRule  implements WageRule {

    private final String sundayWageName;
    private final String publicHolidayWageName;

    public SundayAndPublicHolidayRule(String sundayWageName, String publicHolidayWageName) {
        this.sundayWageName = Objects.requireNonNull(sundayWageName);
        this.publicHolidayWageName = Objects.requireNonNull(publicHolidayWageName);

        if (sundayWageName.isBlank() || publicHolidayWageName.isBlank()) {
            throw new IllegalArgumentException(
                    "Wage names cannot be blank"
            );
        }
    }

    @Override
    public List<Wage> evaluate(DailyRecord dailyRecord) {
        Duration workedDuration =
                AttendanceCalculator.totalDuration(dailyRecord);

        // No work means no Sunday or public holiday wage.
        if (workedDuration.isZero()) {
            return List.of();
        }

        // Public Holiday Takes precedence over Sunday
        if (dailyRecord.publicHoliday()){
            return List.of(new Wage(
                    publicHolidayWageName,
                    dailyRecord.date(),
                    workedDuration
                    ));
        }

        if(dailyRecord.isSunday()){
            return List.of(new Wage(
                    sundayWageName,
                    dailyRecord.date(),
                    workedDuration
            ));
        }

        //Neither Sunday nor Public Holiday
        return List.of();
    }
}
