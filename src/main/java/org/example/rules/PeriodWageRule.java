package org.example.rules;

import org.example.model.DailyRecord;
import org.example.model.Wage;

import java.util.List;

public interface PeriodWageRule {
    List<Wage> evaluate(
            List<DailyRecord> dailyRecord,
            List<Wage> dailyWages
    );
}
