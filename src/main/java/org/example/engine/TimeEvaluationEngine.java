package org.example.engine;

import org.example.config.RuleConfiguration;
import org.example.config.RuleFactory;
import org.example.model.DailyRecord;
import org.example.model.Wage;
import org.example.rules.PeriodWageRule;
import org.example.rules.WageRule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class TimeEvaluationEngine {

    private final List<WageRule> dailyRules;
    private final List<PeriodWageRule> periodRules;

    public TimeEvaluationEngine(RuleConfiguration configuration) {
        RuleFactory ruleFactory = new RuleFactory();

        this.dailyRules = ruleFactory.createDailyRules(configuration);
        this.periodRules = ruleFactory.createPeriodRules(configuration);
    }

    public List<Wage> evaluate(
            List<DailyRecord> dailyRecord){

        Map<LocalDate, DailyRecord> informationByDate =
                new HashMap<>();

        for (DailyRecord info : dailyRecord) {
            if (informationByDate.put(info.date(), info) != null) {
                throw new IllegalArgumentException(
                        "Duplicate daily information for " + info.date()
                );
            }
        }

        List<Wage> dailyWages = new ArrayList<>();

        // Evaluate all daily rules for every attendance day.
        for (DailyRecord attendance: dailyRecord) {
            DailyRecord info = informationByDate.get(attendance.date());


            if (info == null) {
                throw new IllegalArgumentException(
                        "Missing daily information for " + attendance.date()
                );
            }

            // Defensive check: ensure attendance and daily information match.
            if (!attendance.date().equals(info.date())) {
                throw new IllegalArgumentException(
                        "Attendance date and daily information date do not match"
                );
            }

            for(WageRule wageRule: dailyRules){
                dailyWages.addAll(wageRule.evaluate(info));
            }
        }

        List<Wage> allWages = new ArrayList<>(dailyWages);

        // Group attendance days by Monday of each week.
        Map<LocalDate, List<DailyRecord>> attendanceByWeek =
                new TreeMap<>();

        for (DailyRecord attendance : dailyRecord) {
            LocalDate weekStart = attendance.date().with(
                    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            );

            attendanceByWeek
                    .computeIfAbsent(weekStart, key -> new ArrayList<>())
                    .add(attendance);
        }

        // Evaluate period rules separately for each week.
        for(Map.Entry<LocalDate, List<DailyRecord>> entry :attendanceByWeek.entrySet()){
            LocalDate weekStart = entry.getKey();
            List<DailyRecord> weekAttendance = entry.getValue();

            List<Wage> weekDailyWages = dailyWages.stream()
                    .filter(wage -> {
                        LocalDate wageWeekStart = wage.date().with(
                                TemporalAdjusters.previousOrSame(
                                        DayOfWeek.MONDAY
                                )
                        );

                        return wageWeekStart.equals(weekStart);
                    })
                    .toList();

            for (PeriodWageRule rule : periodRules) {
                allWages.addAll(
                        rule.evaluate(
                                weekAttendance,
                                weekDailyWages
                        )
                );
            }
        }

        return List.copyOf(allWages);
    }

}
