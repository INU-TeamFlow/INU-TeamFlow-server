package com.inuteamflow.server.domain.event.entity;

import com.inuteamflow.server.domain.event.dto.Recurrence;
import com.inuteamflow.server.domain.event.enums.RecurrenceFrequency;
import com.inuteamflow.server.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "recurrence_rule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecurrenceRule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurrence_rule_id")
    private Long recurrenceRuleId;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "freq")
    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency freq;

    @Column(name = "interval_value")
    private Integer intervalValue;

    @Column(name = "by_day")
    @Enumerated(EnumType.STRING)
    private DayOfWeek byDay;

    @Column(name = "by_month_day")
    private Integer byMonthDay;

    @Column(name = "series_start_at")
    private LocalDateTime seriesStartAt;

    @Column(name = "until_at")
    private LocalDateTime untilAt;

    @Column(name = "occurrence_count")
    private Integer occurrenceCount;

    @Column(name = "time_zone")
    private String timeZone;

    @Builder
    private RecurrenceRule(
            Long eventId,
            RecurrenceFrequency freq,
            Integer intervalValue,
            DayOfWeek byDay,
            Integer byMonthDay,
            LocalDateTime seriesStartAt,
            LocalDateTime untilAt,
            Integer occurrenceCount,
            String timeZone
    ) {
        this.eventId = eventId;
        this.freq = freq;
        this.intervalValue = intervalValue;
        this.byDay = byDay;
        this.byMonthDay = byMonthDay;
        this.seriesStartAt = seriesStartAt;
        this.untilAt = untilAt;
        this.occurrenceCount = occurrenceCount;
        this.timeZone = timeZone;
    }

    public static RecurrenceRule create(
            Long eventId,
            Recurrence recurrence,
            LocalDateTime seriesStartAt
    ) {
        return RecurrenceRule.builder()
                .eventId(eventId)
                .freq(recurrence.getRecurrenceFrequency())
                .intervalValue(recurrence.getIntervalValue())
                .byDay(recurrence.getByDay())
                .byMonthDay(recurrence.getByMonthDay())
                .seriesStartAt(seriesStartAt)
                .untilAt(recurrence.getUntilAt())
                .occurrenceCount(recurrence.getOccurrenceCount())
                .timeZone(recurrence.getTimeZone())
                .build();
    }

    public void update(
            Recurrence recurrence,
            LocalDateTime seriesStartAt
    ) {
        this.freq = recurrence.getRecurrenceFrequency();
        this.intervalValue = recurrence.getIntervalValue();
        this.byDay = recurrence.getByDay();
        this.byMonthDay = recurrence.getByMonthDay();
        this.seriesStartAt = seriesStartAt;
        this.untilAt = recurrence.getUntilAt();
        this.occurrenceCount = recurrence.getOccurrenceCount();
        this.timeZone = recurrence.getTimeZone();
    }

    public void finishBefore(LocalDateTime occurrenceAt) {
        this.untilAt = occurrenceAt.minusNanos(1);
        this.occurrenceCount = null;
    }
}
