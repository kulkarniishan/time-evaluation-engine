package org.example.util;

import org.example.model.AttendanceInterval;
import org.example.model.DailyRecord;

import java.time.Duration;

public final class AttendanceCalculator {

    private AttendanceCalculator() {
    }

    public static Duration totalDuration(DailyRecord dailyRecord) {
        return dailyRecord.intervals()
                .stream()
                .map(AttendanceInterval::duration)
                .reduce(Duration.ZERO, Duration::plus);
    }
}