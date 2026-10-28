# Configurable Time Evaluation Engine

## Overview

A Java application that evaluates employee attendance records using configurable wage rules. It supports daily calculations, special-day wages, and weekly overtime reconciliation.

## Requirements

- JDK 25
- Maven 3.10.0

## Run the Application

Build the project:

```bash
mvn clean package
```

Run with the example attendance file and rule configuration:

```bash
mvn exec:java -Dexec.args="test-attendance.json rules.json"
```

The command above assumes the application accepts the attendance file and rules file as positional arguments, in that order.

The Maven Exec Plugin must be configured in `pom.xml`, and the application must have a main class.

## Configuration

Rules are configured in `rules.json`. Each rule has a type, rule-specific parameters, and an optional `enabled` flag.

Attendance data is provided through a JSON file. Each daily record contains the date, attendance intervals, public-holiday status, expected working duration, and expected working window.

Example inputs and configurations are available in the repository.

## Supported rules

- Attendance duration
- Daily overtime
- Time outside the expected working window
- Sunday work
- Public-holiday work
- Weekly overtime reconciliation

## Design & Architecture

![System Architecture](docs/architecture.png)

Daily rules evaluate individual `DailyRecord` objects. Period rules evaluate a collection of records for one week and can use daily wages to avoid double-counting overtime.

`RuleFactory` constructs rule implementations from configuration, while `TimeEvaluationEngine` orchestrates daily and weekly evaluation.

## Run the tests

```bash
mvn test
```
Test Output
![Test Results](docs/test-results.png)

## Assumptions and trade-offs

### Assumptions
- One employee is evaluated.
- Attendance intervals are valid, non-overlapping, and contained within a single day.
- Time is evaluated to minute precision.
- Weeks run Monday through Sunday.
- Input covers complete evaluation periods.
- Public-holiday wages take precedence over Sunday wages.
- Weekly overtime is calculated as the excess over 40 hours, less daily overtime already accounted for.

### Trade-offs
- Zero-duration wages are retained for attendance duration, daily overtime, and outside-window rules. Sunday and public-holiday wages are omitted when no attendance occurred.
- Existing rules can be combined and configured without code changes. Adding a new rule type requires registering its implementation in the rule factory.

