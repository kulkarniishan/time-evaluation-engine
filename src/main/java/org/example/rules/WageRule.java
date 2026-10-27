package org.example.rules;

import org.example.model.DailyRecord;
import org.example.model.Wage;

import java.util.List;

public interface WageRule {

    List<Wage> evaluate(
            DailyRecord dailyRecord
    );
}