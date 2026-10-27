package org.example;

import org.example.config.AttendanceInput;
import org.example.config.AttendanceInputLoader;
import org.example.config.RuleConfiguration;
import org.example.config.RuleConfigurationLoader;
import org.example.engine.TimeEvaluationEngine;
import org.example.model.Wage;

import java.time.Duration;
import java.util.List;

public class Main {

    static void main() throws Exception {

        // Load the rule configuration.
        RuleConfiguration configuration =
                new RuleConfigurationLoader().load();

        // Load attendance and daily information.
        AttendanceInput attendanceInput =
                new AttendanceInputLoader().load();

        // Create and run the engine.
        TimeEvaluationEngine engine =
                new TimeEvaluationEngine(configuration);

        List<Wage> wages = engine.evaluate(
                attendanceInput.dailyRecord()
        );

        // Print the results as a table.
        printWages(wages);
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

    private static String formatDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = Math.abs((totalSeconds % 3600) / 60);

        return String.format("%02d:%02d", hours, minutes);
    }
}
