# Configurable Time Evaluation Engine

## Overview

A Java application that evaluates employee attendance records using configurable wage rules. It supports daily calculations, special-day wages, and weekly overtime reconciliation.

## Requirements

- JDK 25
- Maven 3.10.0

## Run the tests

```bash
mvn test
```

## Run the application

```bash
mvn clean package
mvn exec:java
```

Ensure the Maven Exec Plugin is configured and the application has a main class.

## Configuration

Rules are configured in `rules.json`. Each rule has a type, rule-specific parameters, and an optional `enabled` flag.

The attendance input is provided through `attendance.json`. Each daily record contains the date, attendance intervals, public-holiday status, expected working duration, and expected working window.

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

## Assumptions and trade-offs

- One employee is evaluated.
- Attendance intervals are valid, non-overlapping, and contained within a single day.
- Time is evaluated to minute precision.
- Weeks run Monday through Sunday.
- Input covers complete evaluation periods.
- Public-holiday wages take precedence over Sunday wages.
- Weekly overtime is calculated as the excess over 40 hours, less daily overtime already accounted for.
- Zero-duration wages are retained for attendance duration, daily overtime, and outside-window rules. Sunday and public-holiday wages are omitted when no attendance occurred.
- Adding a new rule implementation requires registering it in the rule factory; combining and enabling existing rules does not require code changes.
