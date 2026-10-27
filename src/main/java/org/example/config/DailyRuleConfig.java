package org.example.config;

import java.time.DayOfWeek;
import java.util.List;

public record DailyRuleConfig(
        String type,
        String wageName,
        List<DayOfWeek> applicableDays,
        String sundayWageName,
        String publicHolidayWageName,
        Boolean enabled
) {
    public DailyRuleConfig {
        enabled = enabled == null || enabled;
        applicableDays = applicableDays == null
                ? List.of()
                : List.copyOf(applicableDays);
    }
}
