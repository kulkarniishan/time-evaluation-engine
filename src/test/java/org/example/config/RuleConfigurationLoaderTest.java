
package org.example.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleConfigurationLoaderTest {

    @Test
    void loadsConfigurationFromJson() throws Exception {
        RuleConfiguration configuration =
                new RuleConfigurationLoader().load();

        assertNotNull(configuration);
        assertNotNull(configuration.dailyRules());
        assertNotNull(configuration.periodRules());

        assertFalse(configuration.dailyRules().isEmpty());
        assertFalse(configuration.periodRules().isEmpty());
    }

    @Test
    void loadsDailyOvertimeSettings() throws Exception {
        RuleConfiguration configuration =
                new RuleConfigurationLoader().load();

        DailyRuleConfig rule = configuration.dailyRules()
                .stream()
                .filter(r -> r.type().equals("dailyOvertime"))
                .findFirst()
                .orElseThrow();

        assertEquals("dailyOvertime", rule.wageName());
        assertTrue(rule.enabled());

        assertTrue(rule.applicableDays().contains(
                java.time.DayOfWeek.MONDAY));
        assertTrue(rule.applicableDays().contains(
                java.time.DayOfWeek.FRIDAY));
        assertFalse(rule.applicableDays().contains(
                java.time.DayOfWeek.SATURDAY));
    }

    @Test
    void loadsWeeklyOvertimeSettings() throws Exception {
        RuleConfiguration configuration =
                new RuleConfigurationLoader().load();

        PeriodRuleConfig rule = configuration.periodRules()
                .stream()
                .filter(r -> r.type().equals("weeklyOvertime"))
                .findFirst()
                .orElseThrow();

        assertEquals("weeklyOvertime", rule.wageName());
        assertEquals("dailyOvertime", rule.dailyOvertimeWageName());
        assertEquals(40L, rule.weeklyThresholdHours());
        assertTrue(rule.enabled());
    }

    @Test
    void loadsAttendanceDurationRule() throws Exception {
        RuleConfiguration configuration =
                new RuleConfigurationLoader().load();

        DailyRuleConfig rule = configuration.dailyRules()
                .stream()
                .filter(r -> r.type().equals("attendanceDuration"))
                .findFirst()
                .orElseThrow();

        assertEquals("attendanceDuration", rule.wageName());
        assertTrue(rule.enabled());
    }

    @Test
    void loadsSundayAndPublicHolidayRule() throws Exception {
        RuleConfiguration configuration =
                new RuleConfigurationLoader().load();

        DailyRuleConfig rule = configuration.dailyRules()
                .stream()
                .filter(r -> r.type().equals("sundayAndPublicHoliday"))
                .findFirst()
                .orElseThrow();

        assertEquals("sundayWork", rule.sundayWageName());
        assertEquals("publicHolidayWork", rule.publicHolidayWageName());
        assertTrue(rule.enabled());
    }
}
