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
import com.inuteamflow.server.domain.team.entity.Team;
import com.inuteamflow.server.domain.team.entity.TeamMember;
import com.inuteamflow.server.domain.team.enums.TeamRole;
import com.inuteamflow.server.domain.team.repository.TeamMemberRepository;
import com.inuteamflow.server.domain.team.repository.TeamRepository;
import com.inuteamflow.server.domain.user.entity.User;
import com.inuteamflow.server.global.exception.error.CustomErrorCode;
import com.inuteamflow.server.global.exception.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public List<EventListResponse> getTeamEventList(
            User user,
            Long teamId,
            Integer year,
            Integer month
    ) {
        Team team = getTeam(teamId);
        validateTeamMember(team, user);
        EventOccurrenceService.DateRange dateRange = eventOccurrenceService.createMonthlyDateRange(year, month);

        List<Event> singleEvents = eventRepository.findByTeamAndEventKindAndStartAtBeforeAndEndAtAfter(
                team,
                EventKind.SINGLE,
                dateRange.endAt(),
                dateRange.startAt()
        );
        List<Event> recurringEvents = eventRepository.findByTeamAndEventKindAndStartAtBefore(
                team,
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
        Team team = getTeam(teamId);
        TeamMember host = validateTeamMember(team, user);
        Event event = eventRepository.save(Event.create(team, request));
        RecurrenceRule recurrenceRule = eventRecurrenceService.createRecurrenceRule(event, request);
        createParticipants(event, team, host, request);

        return EventDetailResponse.create(event, recurrenceRule);
    }

    @Transactional
    public EventDetailResponse updateTeamEvent(
            User user,
            Long teamId,
            Long eventId,
            TeamEventUpdateRequest request
    ) {
        Event event = getTeamEvent(teamId, eventId);
        validateEventManager(event.getTeam(), user, event);

        return eventRecurrenceService.updateEvent(event, event.getTeam(), request);
    }

    @Transactional
    public void deleteTeamEvent(
            User user,
            Long teamId,
            Long eventId,
            RecurrenceEditScope recurrenceEditScope,
            LocalDateTime occurrenceAt
    ) {
        Event event = getTeamEvent(teamId, eventId);
        validateEventManager(event.getTeam(), user, event);

        if (eventRecurrenceService.deleteEvent(event, recurrenceEditScope, occurrenceAt)) {
            eventParticipantRepository.deleteByEvent_EventId(event.getEventId());
            eventRepository.delete(event);
        }
    }

    private void createParticipants(
            Event event,
            Team team,
            TeamMember host,
            TeamEventCreateRequest request
    ) {
        List<EventParticipant> participants = new ArrayList<>();
        participants.add(EventParticipant.create(event, host, EventRole.HOST));

        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            request.getParticipants().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .filter(teamMemberId -> !teamMemberId.equals(host.getTeamMemberId()))
                    .map(teamMemberId -> EventParticipant.create(
                            event,
                            getTeamMember(team, teamMemberId),
                            EventRole.PARTICIPANT
                    ))
                    .forEach(participants::add);
        }

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

    private Team getTeam(
            Long teamId
    ) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.TEAM_NOT_FOUND));
    }

    private TeamMember getTeamMember(
            Team team,
            Long teamMemberId
    ) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.TEAM_MEMBER_NOT_FOUND));

        if (!team.getTeamId().equals(teamMember.getTeam().getTeamId())) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        return teamMember;
    }

    private TeamMember validateTeamMember(
            Team team,
            User user
    ) {
        return teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.TEAM_MEMBER_NOT_FOUND));
    }

    private void validateEventManager(
            Team team,
            User user,
            Event event
    ) {
        TeamMember teamMember = validateTeamMember(team, user);
        if (event.getCreatedBy().equals(user.getUserId())
                || teamMember.getTeamRole() == TeamRole.LEADER
                || teamMember.getTeamRole() == TeamRole.MANAGER) {
            return;
        }

        throw new RestApiException(CustomErrorCode.TEAM_FORBIDDEN);
    }
}
