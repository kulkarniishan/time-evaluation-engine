
package org.example.config;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleConfigurationTest {

    @Test
    void defaultsNullEnabledToTrue() {
        DailyRuleConfig rule = new DailyRuleConfig(
                "attendanceDuration",
                "attendanceDuration",
                List.of(),
                null,
                null,
                null
        );

        assertTrue(rule.enabled());
    }

    @Test
    void preservesExplicitlyDisabledRule() {
        DailyRuleConfig rule = new DailyRuleConfig(
                "attendanceDuration",
                "attendanceDuration",
                List.of(),
                null,
                null,
                false
        );

        assertFalse(rule.enabled());
    }

    @Test
    void defaultsNullApplicableDaysToEmptyList() {
        DailyRuleConfig rule = new DailyRuleConfig(
                "dailyOvertime",
                "dailyOvertime",
                null,
                null,
                null,
                true
        );

        assertNotNull(rule.applicableDays());
        assertTrue(rule.applicableDays().isEmpty());
    }

    @Test
    void copiesApplicableDaysDefensively() {
        List<DayOfWeek> days = new ArrayList<>(
                List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        );

        DailyRuleConfig rule = new DailyRuleConfig(
                "dailyOvertime",
                "dailyOvertime",
                days,
                null,
                null,
                true
        );

        days.clear();

        assertEquals(
                List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                rule.applicableDays()
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> rule.applicableDays().add(DayOfWeek.SUNDAY)
        );
    }

    @Test
    void copiesRuleListsDefensively() {
        List<DailyRuleConfig> dailyRules = new ArrayList<>();
        List<PeriodRuleConfig> periodRules = new ArrayList<>();

        dailyRules.add(new DailyRuleConfig(
                "attendanceDuration",
                "attendanceDuration",
                List.of(),
                null,
                null,
                true
        ));

        RuleConfiguration configuration =
                new RuleConfiguration(dailyRules, periodRules);

        dailyRules.clear();

        assertEquals(1, configuration.dailyRules().size());

        assertThrows(
                UnsupportedOperationException.class,
                () -> configuration.dailyRules().clear()
        );
    }

    @Test
    void defaultsNullPeriodRuleEnabledToTrue() {
        PeriodRuleConfig rule = new PeriodRuleConfig(
                "weeklyOvertime",
                "weeklyOvertime",
                "dailyOvertime",
                40L,
                null
        );

        assertTrue(rule.enabled());
    }

    @Test
    void preservesDisabledPeriodRule() {
        PeriodRuleConfig rule = new PeriodRuleConfig(
                "weeklyOvertime",
                "weeklyOvertime",
                "dailyOvertime",
                40L,
                false
        );

        assertFalse(rule.enabled());
    }
}
