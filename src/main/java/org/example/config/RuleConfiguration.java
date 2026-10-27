
package org.example.config;

import java.util.List;

public record RuleConfiguration(
        List<DailyRuleConfig> dailyRules,
        List<PeriodRuleConfig> periodRules
) {
    public RuleConfiguration {
        dailyRules = List.copyOf(dailyRules);
        periodRules = List.copyOf(periodRules);
    }
}
