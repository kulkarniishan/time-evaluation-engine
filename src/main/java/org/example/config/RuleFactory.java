
package org.example.config;

import org.example.rules.*;

import java.time.Duration;
import java.util.List;

public class RuleFactory {

    public List<WageRule> createDailyRules(
            RuleConfiguration configuration
    ) {
        return configuration.dailyRules().stream()
                .filter(rule -> Boolean.TRUE.equals(rule.enabled()))
                .map(this::createDailyRule)
                .toList();
    }

    private WageRule createDailyRule(DailyRuleConfig config) {
        return switch (config.type()) {
            case "attendanceDuration" ->
                    new AttendanceDurationRule(config.wageName());

            case "dailyOvertime" ->
                    new DailyOvertimeRule(
                            java.util.Set.copyOf(config.applicableDays()),
                            config.wageName()
                    );

            case "outsideExpectedWindow" ->
                    new OutsideExpectedWindowRule(config.wageName());

            case "sundayAndPublicHoliday" ->
                    new SundayAndPublicHolidayRule(
                            config.sundayWageName(),
                            config.publicHolidayWageName()
                    );

            default -> throw new IllegalArgumentException(
                    "Unknown daily rule type: " + config.type()
            );
        };
    }

    public List<PeriodWageRule> createPeriodRules(
            RuleConfiguration configuration
    ) {
        return configuration.periodRules().stream()
                .filter(rule -> Boolean.TRUE.equals(rule.enabled()))
                .map(this::createPeriodRule)
                .toList();
    }

    private PeriodWageRule createPeriodRule(PeriodRuleConfig config) {
        return switch (config.type()) {
            case "weeklyOvertime" ->
                    new WeeklyOvertimeRule(
                            config.wageName(),
                            config.dailyOvertimeWageName(),
                            Duration.ofHours(config.weeklyThresholdHours())
                    );

            default -> throw new IllegalArgumentException(
                    "Unknown period rule type: " + config.type()
            );
        };
    }
}
