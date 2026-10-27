
package org.example.config;

import org.example.model.DailyRecord;

import java.util.List;

public record AttendanceInput(
        List<DailyRecord> dailyRecord
) {
    public AttendanceInput {
        dailyRecord = List.copyOf(dailyRecord);
    }
}
