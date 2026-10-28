
package org.example.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

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

        assertEquals("DAILY_OVERTIME", rule.wageName());
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

        assertEquals("WEEKLY_OVERTIME", rule.wageName());
        assertEquals("DAILY_OVERTIME", rule.dailyOvertimeWageName());
        assertEquals(Duration.ofHours(40), rule.weeklyThreshold());
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

        assertEquals("ATTENDANCE_DURATION", rule.wageName());
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

        assertEquals("SUNDAY_WORK", rule.sundayWageName());
        assertEquals("PUBLIC_HOLIDAY_WORK", rule.publicHolidayWageName());
        assertTrue(rule.enabled());
    }
}
