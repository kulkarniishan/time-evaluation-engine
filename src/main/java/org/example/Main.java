package org.example;

import org.example.config.AttendanceInput;
import org.example.config.AttendanceInputLoader;
import org.example.config.RuleConfiguration;
import org.example.config.RuleConfigurationLoader;
import org.example.engine.TimeEvaluationEngine;
import org.example.model.Wage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class Main {

    static void main(String[] args) throws Exception {
        String attendancePath = args.length > 0 ? args[0] : null;
        String rulesPath = args.length > 1 ? args[1] : null;

        // Load the rule configuration.
        RuleConfiguration configuration =
                new RuleConfigurationLoader().load(rulesPath);

        // Load attendance and daily information.
        AttendanceInput attendanceInput =
                new AttendanceInputLoader().load(attendancePath);

        // Create and run the engine.
        TimeEvaluationEngine engine =
                new TimeEvaluationEngine(configuration);

        List<Wage> wages = engine.evaluate(
                attendanceInput.dailyRecords()
        );

        // Print the results as a table.
        printWages(wages);

        //Print Wages Grouped by Date
        printWagesGroupedByDate(wages);
    }

    private static void printWages(List<Wage> wages) {
        String line = "+--------------------------+------------+------------------+";

        System.out.println();
        System.out.println("                 TIME EVALUATION RESULTS");
        System.out.println(line);
        System.out.printf("| %-24s | %-10s | %-16s |%n",
                "Wage Name", "Date", "Duration");
        System.out.println(line);

        for (Wage wage : wages) {
            System.out.printf("| %-24s | %-10s | %-16s |%n",
                    wage.name(),
                    wage.date(),
                    formatDuration(wage.duration()));
        }

        System.out.println(line);
        System.out.println("Total wage entries: " + wages.size());
    }

    private static void printWagesGroupedByDate(List<Wage> wages) {
        Map<LocalDate, Map<String, Duration>> grouped = new TreeMap<>();

        Set<String> wageNames = new TreeSet<>();

        for (Wage wage : wages) {
            wageNames.add(wage.name());

            grouped
                    .computeIfAbsent(wage.date(), date -> new HashMap<>())
                    .merge(wage.name(), wage.duration(), Duration::plus);
        }

        int dateWidth = 12;
        int wageWidth = 24;

        String separator = "+" + "-".repeat(dateWidth + 2) + "+"
                + ("-".repeat(wageWidth + 2) + "+").repeat(wageNames.size());

        System.out.println();
        System.out.println("                    GROUPED BY DATE");
        System.out.println(separator);

        System.out.printf("| %-" + dateWidth + "s |", "Date");

        for (String name : wageNames) {
            System.out.printf(" %-" + wageWidth + "s |", name);
        }
        System.out.println();

        System.out.println(separator);

        for (Map.Entry<LocalDate, Map<String, Duration>> entry : grouped.entrySet()) {
            System.out.printf("| %-" + dateWidth + "s |", entry.getKey());

            for (String name : wageNames) {
                Duration duration = entry.getValue().get(name);

                String value = duration == null
                        ? "-"
                        : formatDuration(duration);

                System.out.printf(" %-" + wageWidth + "s |", value);
            }

            System.out.println();
        }

        System.out.println(separator);
        System.out.println("Total dates: " + grouped.size());
    }

    private static String formatDuration(Duration duration) {
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = Math.abs(totalMinutes % 60);

        return String.format("%dh %02dm", hours, minutes);
    }
}
