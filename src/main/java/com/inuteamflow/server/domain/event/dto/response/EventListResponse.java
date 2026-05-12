package com.inuteamflow.server.domain.event.dto.response;

import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.event.entity.RecurrenceException;
import com.inuteamflow.server.domain.event.enums.EventColor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventListResponse {

    private Long eventId;
    private Long teamId;
    private String teamName;

    private String title;
    private String description;

    private LocalDateTime occurrenceAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Boolean isAllDay;
    private EventColor color;

    private Boolean isSingle;
    private Boolean isFinished;
    private Boolean isException;

    public static EventListResponse createSingle(Event event) {
        return new EventListResponse(
                event.getEventId(),
                event.getTeamId(),
                null,
                event.getTitle(),
                event.getDescription(),
                null,
                event.getStartAt(),
                event.getEndAt(),
                event.getIsAllDay(),
                event.getColor(),
                event.getIsSingle(),
                event.getIsFinished(),
                false
        );
    }

    public static EventListResponse createOccurrence(
            Event event,
            LocalDateTime occurrenceAt,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return new EventListResponse(
                event.getEventId(),
                event.getTeamId(),
                null,
                event.getTitle(),
                event.getDescription(),
                occurrenceAt,
                startAt,
                endAt,
                event.getIsAllDay(),
                event.getColor(),
                event.getIsSingle(),
                event.getIsFinished(),
                false
        );
    }

    public static EventListResponse createModifiedOccurrence(
            Event event,
            RecurrenceException recurrenceException
    ) {
        return new EventListResponse(
                event.getEventId(),
                event.getTeamId(),
                null,
                recurrenceException.getModifiedTitle(),
                recurrenceException.getModifiedDescription(),
                recurrenceException.getOriginalOccurrenceAt(),
                recurrenceException.getModifiedStartAt(),
                recurrenceException.getModifiedEndAt(),
                recurrenceException.getModifiedIsAllDay(),
                recurrenceException.getModifiedColor(),
                event.getIsSingle(),
                event.getIsFinished(),
                true
        );
    }
}
