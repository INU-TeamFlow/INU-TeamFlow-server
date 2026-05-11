package com.inuteamflow.server.domain.event.service;

import com.inuteamflow.server.domain.event.dto.request.MyEventCreateRequest;
import com.inuteamflow.server.domain.event.dto.request.MyEventUpdateRequest;
import com.inuteamflow.server.domain.event.dto.response.EventDetailResponse;
import com.inuteamflow.server.domain.event.dto.response.EventListResponse;
import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.event.entity.RecurrenceRule;
import com.inuteamflow.server.domain.event.enums.EventKind;
import com.inuteamflow.server.domain.event.enums.RecurrenceEditScope;
import com.inuteamflow.server.domain.event.repository.EventRepository;
import com.inuteamflow.server.domain.user.entity.User;
import com.inuteamflow.server.global.exception.error.CustomErrorCode;
import com.inuteamflow.server.global.exception.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyEventService {

    private final EventOccurrenceService eventOccurrenceService;
    private final EventRecurrenceService eventRecurrenceService;
    private final EventRepository eventRepository;

    // TODO: Include team events where this user participates after TeamMember is implemented.
    public List<EventListResponse> getMyEventList(
            User user,
            Integer year,
            Integer month
    ) {
        EventOccurrenceService.DateRange dateRange = eventOccurrenceService.createMonthlyDateRange(year, month);

        List<Event> singleEvents = eventRepository.findByCreatedByAndTeamIdIsNullAndEventKindAndStartAtBeforeAndEndAtAfter(
                user.getUserId(),
                EventKind.SINGLE,
                dateRange.endAt(),
                dateRange.startAt()
        );
        List<Event> recurringEvents = eventRepository.findByCreatedByAndTeamIdIsNullAndEventKindAndStartAtBefore(
                user.getUserId(),
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
    public EventDetailResponse createMyEvent(
            User user,
            MyEventCreateRequest request
    ) {
        Event event = eventRepository.save(Event.create(request));
        RecurrenceRule recurrenceRule = eventRecurrenceService.createRecurrenceRule(event, request);

        return EventDetailResponse.create(event, recurrenceRule);
    }

    @Transactional
    public EventDetailResponse updateMyEvent(
            User user,
            Long eventId,
            MyEventUpdateRequest request
    ) {
        Event event = getMyEvent(user, eventId);

        return eventRecurrenceService.updateEvent(event, null, request);
    }

    @Transactional
    public void deleteMyEvent(
            User user,
            Long eventId,
            RecurrenceEditScope recurrenceEditScope,
            LocalDateTime occurrenceAt
    ) {
        Event event = getMyEvent(user, eventId);

        if (eventRecurrenceService.deleteEvent(event, recurrenceEditScope, occurrenceAt)) {
            eventRepository.delete(event);
        }
    }

    private Event getMyEvent(
            User user,
            Long eventId
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.COMMON_HANDLER_NOT_FOUND));

        if (event.getTeamId() != null || !event.getCreatedBy().equals(user.getUserId())) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        return event;
    }
}
