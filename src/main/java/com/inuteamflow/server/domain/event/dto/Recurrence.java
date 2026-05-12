package com.inuteamflow.server.domain.event.dto;

import com.inuteamflow.server.domain.event.entity.RecurrenceRule;
import com.inuteamflow.server.domain.event.enums.RecurrenceFrequency;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recurrence {

    @NotNull
    private RecurrenceFrequency freq;

    @NotNull
    @Positive
    private Integer intervalValue;

    private List<DayOfWeek> byDay;

    @Min(1)
    @Max(31)
    private Integer byMonthDay;

    private LocalDateTime seriesStartAt;

    private LocalDateTime untilAt;

    @Positive
    private Integer occurrenceCount;

    private String timeZone;

    @AssertTrue(message = "untilAt and occurrenceCount cannot be used together")
    public boolean isValidEndCondition() {
        return untilAt == null || occurrenceCount == null;
    }

    @AssertTrue(message = "byDay is required for weekly recurrence")
    public boolean isValidWeeklyByDay() {
        if (freq != RecurrenceFrequency.WEEKLY) {
            return true;
        }

        return byDay != null && !byDay.isEmpty();
    }

    public static Recurrence create(RecurrenceRule recurrenceRule) {
        if (recurrenceRule == null) {
            return null;
        }

        return new Recurrence(
                recurrenceRule.getFreq(),
                recurrenceRule.getIntervalValue(),
                recurrenceRule.getByDay(),
                recurrenceRule.getByMonthDay(),
                recurrenceRule.getSeriesStartAt(),
                recurrenceRule.getUntilAt(),
                recurrenceRule.getOccurrenceCount(),
                recurrenceRule.getTimeZone()
        );
    }
}
