package com.inuteamflow.server.domain.event.entity;

import com.inuteamflow.server.domain.event.dto.EventCreateCommand;
import com.inuteamflow.server.domain.event.dto.EventUpdateCommand;
import com.inuteamflow.server.domain.event.dto.Recurrence;
import com.inuteamflow.server.domain.event.enums.EventKind;
import com.inuteamflow.server.domain.event.enums.EventStatus;
import com.inuteamflow.server.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "is_all_day")
    private Boolean isAllDay;

    @Column(name = "color")
    private String color;

    @Column(name = "uid")
    private String uid;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(name = "event_kind")
    @Enumerated(EnumType.STRING)
    private EventKind eventKind;

    @Builder
    private Event(
            Long teamId,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Boolean isAllDay,
            String color,
            String uid,
            Integer sequence,
            EventStatus status,
            EventKind eventKind
    ) {
        this.teamId = teamId;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isAllDay = isAllDay;
        this.color = color;
        this.uid = uid;
        this.sequence = sequence;
        this.status = status;
        this.eventKind = eventKind;
    }

    public static Event create(
            EventCreateCommand command
    ) {
        return Event.builder()
                .title(command.getTitle())
                .description(command.getDescription())
                .startAt(command.getStartAt())
                .endAt(command.getEndAt())
                .isAllDay(command.getIsAllDay())
                .color(command.getColor())
                .uid(UUID.randomUUID().toString())
                .sequence(0)
                .status(EventStatus.UNFINISHED)
                .eventKind(resolveEventKind(command.getRecurrence()))
                .build();
    }

    public static Event create(
            Long teamId,
            EventCreateCommand command
    ) {
        return Event.builder()
                .teamId(teamId)
                .title(command.getTitle())
                .description(command.getDescription())
                .startAt(command.getStartAt())
                .endAt(command.getEndAt())
                .isAllDay(command.getIsAllDay())
                .color(command.getColor())
                .uid(UUID.randomUUID().toString())
                .sequence(0)
                .status(EventStatus.UNFINISHED)
                .eventKind(resolveEventKind(command.getRecurrence()))
                .build();
    }

    public static Event createRecurring(
            EventUpdateCommand command
    ) {
        return Event.builder()
                .title(command.getTitle())
                .description(command.getDescription())
                .startAt(command.getStartAt())
                .endAt(command.getEndAt())
                .isAllDay(command.getIsAllDay())
                .color(command.getColor())
                .uid(UUID.randomUUID().toString())
                .sequence(0)
                .status(command.getStatus() != null ? command.getStatus() : EventStatus.UNFINISHED)
                .eventKind(EventKind.RECURRING)
                .build();
    }

    public static Event createRecurring(
            Long teamId,
            EventUpdateCommand command
    ) {
        return Event.builder()
                .teamId(teamId)
                .title(command.getTitle())
                .description(command.getDescription())
                .startAt(command.getStartAt())
                .endAt(command.getEndAt())
                .isAllDay(command.getIsAllDay())
                .color(command.getColor())
                .uid(UUID.randomUUID().toString())
                .sequence(0)
                .status(command.getStatus() != null ? command.getStatus() : EventStatus.UNFINISHED)
                .eventKind(EventKind.RECURRING)
                .build();
    }

    public void update(EventUpdateCommand command) {
        this.title = command.getTitle();
        this.description = command.getDescription();
        this.startAt = command.getStartAt();
        this.endAt = command.getEndAt();
        this.isAllDay = command.getIsAllDay();
        this.color = command.getColor();
        if (command.getStatus() != null) {
            this.status = command.getStatus();
        }
    }

    public void changeToRecurring() {
        this.eventKind = EventKind.RECURRING;
    }

    public void increaseSequence() {
        this.sequence++;
    }

    private static EventKind resolveEventKind(Recurrence recurrence) {
        return recurrence == null ? EventKind.SINGLE : EventKind.RECURRING;
    }


}
