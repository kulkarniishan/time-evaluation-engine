
package org.example.config;

public record PeriodRuleConfig(
        String type,
        String wageName,
        String dailyOvertimeWageName,
        Long weeklyThresholdHours,
        Boolean enabled
) {
    public PeriodRuleConfig {
        enabled = enabled == null || enabled;
    }
}
