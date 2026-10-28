
package org.example.config;

import org.example.model.DailyRecord;

import java.util.List;

public record AttendanceInput(
        List<DailyRecord> dailyRecords
) {
    public AttendanceInput {
        dailyRecords = List.copyOf(dailyRecords);
    }
}
