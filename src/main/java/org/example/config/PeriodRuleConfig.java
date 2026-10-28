
package org.example.config;

import java.time.Duration;

public record PeriodRuleConfig(
        String type,
        String wageName,
        String dailyOvertimeWageName,
        Duration weeklyThreshold,
        Boolean enabled
) {
    public PeriodRuleConfig {
        enabled = enabled == null || enabled;
    }
}
