# Configurable Time Evaluation Engine

A Java backend that evaluates attendance data using configurable rules and produces **wages**. A wage here is a named time duration, not a monetary amount. Rules are defined in JSON, so they can be changed and combined without touching application code.

## Requirements

- JDK 25
- Maven 3.9+

## Build and run

```bash
mvn clean package
mvn exec:java -Dexec.args="<attendance-file> <rules-file>"
```

Arguments are positional: attendance file first, rules file second.

Example (Example A from the assignment):

```bash
mvn exec:java -Dexec.args="examples/example-a-attendance.json examples/rules.json"
```

The result is printed to stdout as named wages per date or period.

## Tests

```bash
mvn test
```
![Test Results](docs/test-results.png)

The tests cover attendance duration, daily overtime, time outside the window, Sunday and public-holiday precedence, and the weekly/daily reconciliation (Examples A and B).

## Input format

**Attendance file**: one record per day:

| Field | Description |
|---|---|
| `date` | ISO date, e.g. `2026-10-26` |
| `intervals` | list of `{start, end}` times (HH:mm), may be empty |
| `publicHoliday` | boolean |
| `expectedDuration` | ISO-8601 duration, e.g. `PT8H` |
| `expectedWindow` | `{start, end}` expected working time window |

The weekday is derived from the date.

**Rules file** (`rules.json`): a list of rules. Each rule has a `type`, a wage `name`, rule-specific parameters, and an optional `enabled` flag.
```json
{
  "dailyRules": [
    {
      "type": "attendanceDuration",
      "wageName": "ATTENDANCE_DURATION",
      "enabled": true
    },
    {
      "type": "dailyOvertime",
      "wageName": "DAILY_OVERTIME",
      "applicableDays": [
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY"
      ],
      "enabled": true
    },
    {
      "type": "outsideExpectedWindow",
      "wageName": "OUTSIDE_EXPECTED_WINDOW",
      "enabled": true
    },
    {
      "type": "sundayAndPublicHoliday",
      "sundayWageName": "SUNDAY_WORK",
      "publicHolidayWageName": "PUBLIC_HOLIDAY_WORK",
      "enabled": true
    }
  ],
  "periodRules": [
    {
      "type": "weeklyOvertime",
      "wageName": "WEEKLY_OVERTIME",
      "dailyOvertimeWageName": "DAILY_OVERTIME",
      "weeklyThreshold": "PT40H",
      "enabled": true
    }
  ]
}

```

## Supported rules

| Rule | Result |
|---|---|
| Attendance duration | Sum of the day's intervals; gaps between them are excluded |
| Daily overtime | Attendance beyond the expected duration, never below zero; the applicable weekdays are configurable |
| Outside expected window | Attendance falling outside the expected time window |
| Sunday work | Attendance on a Sunday that is not a public holiday |
| Public-holiday work | Attendance on a public holiday (takes precedence over Sunday) |
| Weekly overtime | Weekly attendance beyond a threshold, reduced by daily overtime already counted |

### Weekly/daily reconciliation

Weekly overtime is reduced by the daily overtime already accounted for, with a minimum of zero. Daily overtime stays unchanged.

With daily overtime above 8 h on Monday to Friday and weekly overtime above 40 h over all seven days:

| Example | Daily OT | Weekly OT (raw) | Weekly OT (reconciled) |
|---|---|---|---|
| A | 4 h | 2 h | 0 h |
| B | 2 h | 6 h | 4 h |

## Design & Architecture

![System Architecture](docs/architecture.png)

- `DailyRecord` holds one day's data.
- **Daily rules** evaluate a single `DailyRecord` and produce wages for that date.
- **Period rules** evaluate all records of one week and can read the daily wages, which is how weekly overtime avoids double-counting.
- `RuleFactory` builds rule instances from the JSON configuration.
- `TimeEvaluationEngine` orchestrates the daily pass, then the weekly pass, and collects the wages.

## Assumptions

- One employee.
- Intervals are valid, non-overlapping, and within a single day.
- Minute precision.
- Weeks run Monday to Sunday.
- Input covers complete evaluation periods.
- A day that is both Sunday and a public holiday produces only the public-holiday wage.

## Trade-offs

- Zero-duration wages are kept for attendance duration, daily overtime and outside-window rules. Sunday and public-holiday wages are omitted when there was no attendance.
- Existing rules can be configured and combined without code changes. A new rule *type* needs a Java implementation registered in `RuleFactory`.
- JSON was chosen over a custom DSL for simplicity and tooling support.

## Not done / limitations

- Monthly aggregation is not implemented.
- Rules are evaluated in the configured order; there is no dependency resolution between rules beyond the daily-then-weekly passes.
- Minimal validation of malformed configuration.