package com.inuteamflow.server.domain.event.service;

import com.inuteamflow.server.domain.event.dto.request.TeamEventCreateRequest;
import com.inuteamflow.server.domain.event.dto.request.TeamEventUpdateRequest;
import com.inuteamflow.server.domain.event.dto.response.EventDetailResponse;
import com.inuteamflow.server.domain.event.dto.response.EventListResponse;
import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.event.entity.EventParticipant;
import com.inuteamflow.server.domain.event.entity.RecurrenceRule;
import com.inuteamflow.server.domain.event.enums.EventKind;
import com.inuteamflow.server.domain.event.enums.EventRole;
import com.inuteamflow.server.domain.event.enums.RecurrenceEditScope;
import com.inuteamflow.server.domain.event.repository.EventParticipantRepository;
import com.inuteamflow.server.domain.event.repository.EventRepository;
import com.inuteamflow.server.domain.user.entity.User;
import com.inuteamflow.server.global.exception.error.CustomErrorCode;
import com.inuteamflow.server.global.exception.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamEventService {

    private final EventOccurrenceService eventOccurrenceService;
    private final EventRecurrenceService eventRecurrenceService;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    // TODO: Validate team existence and membership after Team domain is implemented.
    public List<EventListResponse> getTeamEventList(
            Long teamId,
            Integer year,
            Integer month
    ) {
        EventOccurrenceService.DateRange dateRange = eventOccurrenceService.createMonthlyDateRange(year, month);

        List<Event> singleEvents = eventRepository.findByTeamIdAndEventKindAndStartAtBeforeAndEndAtAfter(
                teamId,
                EventKind.SINGLE,
                dateRange.endAt(),
                dateRange.startAt()
        );
        List<Event> recurringEvents = eventRepository.findByTeamIdAndEventKindAndStartAtBefore(
                teamId,
                EventKind.RECURRING,
                dateRange.endAt()
        );
        List<EventListResponse> recurringOccurrences = eventOccurrenceService.expandRecurringEvents(
                recurringEvents,
                dateRange
        );

        return eventOccurrenceService.mergeAndSort(singleEvents, recurringOccurrences);
    }

    @Transactional
    public EventDetailResponse createTeamEvent(
            User user,
            Long teamId,
            TeamEventCreateRequest request
    ) {
        // TODO: Validate team existence and creator membership after Team domain is implemented.
        Event event = eventRepository.save(Event.create(teamId, request));
        RecurrenceRule recurrenceRule = eventRecurrenceService.createRecurrenceRule(event, request);
        createParticipants(event, request);

        return EventDetailResponse.create(event, recurrenceRule);
    }

    @Transactional
    public EventDetailResponse updateTeamEvent(
            User user,
            Long teamId,
            Long eventId,
            TeamEventUpdateRequest request
    ) {
        // TODO: Validate team existence and user membership after Team domain is implemented.
        Event event = getTeamEvent(teamId, eventId);

        return eventRecurrenceService.updateEvent(event, event.getTeamId(), request);
    }

    @Transactional
    public void deleteTeamEvent(
            User user,
            Long teamId,
            Long eventId,
            RecurrenceEditScope recurrenceEditScope,
            LocalDateTime occurrenceAt
    ) {
        // TODO: Validate team existence and user membership after Team domain is implemented.
        Event event = getTeamEvent(teamId, eventId);

        if (eventRecurrenceService.deleteEvent(event, recurrenceEditScope, occurrenceAt)) {
            eventParticipantRepository.deleteByEventId(event.getEventId());
            eventRepository.delete(event);
        }
    }

    private void createParticipants(
            Event event,
            TeamEventCreateRequest request
    ) {
        if (request.getParticipants() == null || request.getParticipants().isEmpty()) {
            return;
        }

        List<EventParticipant> participants = request.getParticipants().stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(teamMemberId -> EventParticipant.create(
                        event.getEventId(),
                        teamMemberId,
                        EventRole.PARTICIPANT
                ))
                .toList();

        eventParticipantRepository.saveAll(participants);
    }

    private Event getTeamEvent(
            Long teamId,
            Long eventId
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.COMMON_HANDLER_NOT_FOUND));

        if (!teamId.equals(event.getTeamId())) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        return event;
    }
}
