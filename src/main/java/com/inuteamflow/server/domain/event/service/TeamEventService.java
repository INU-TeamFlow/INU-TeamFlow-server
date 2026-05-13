package com.inuteamflow.server.domain.event.service;

import com.inuteamflow.server.domain.event.dto.request.TeamEventCreateRequest;
import com.inuteamflow.server.domain.event.dto.request.TeamEventUpdateRequest;
import com.inuteamflow.server.domain.event.dto.response.EventDetailResponse;
import com.inuteamflow.server.domain.event.dto.response.EventListResponse;
import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.event.entity.EventParticipant;
import com.inuteamflow.server.domain.event.entity.RecurrenceRule;
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

        List<Event> singleEvents = eventRepository.findByTeamAndIsSingleAndStartAtBeforeAndEndAtAfter(
                team,
                true,
                dateRange.endAt(),
                dateRange.startAt()
        );
        List<Event> recurringEvents = eventRepository.findByTeamAndIsSingleAndStartAtBefore(
                team,
                false,
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
        TeamMember host = validateTeamEventManager(team, user);
        Event event = eventRepository.save(Event.create(team, request));
        RecurrenceRule recurrenceRule = eventRecurrenceService.createRecurrenceRule(event, request);
        createParticipants(event, team, host, request.getParticipants());

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
        validateTeamEventManager(event.getTeam(), user);

        EventDetailResponse response = eventRecurrenceService.updateEvent(event, event.getTeam(), request);
        syncParticipants(response.getEventId(), event.getTeam(), request.getParticipants());

        return response;
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
        validateTeamEventManager(event.getTeam(), user);

        if (eventRecurrenceService.deleteEvent(event, recurrenceEditScope, occurrenceAt)) {
            eventParticipantRepository.deleteByEvent_EventId(event.getEventId());
            eventRepository.delete(event);
        }
    }

    private void createParticipants(
            Event event,
            Team team,
            TeamMember host,
            List<Long> participantIds
    ) {
        List<EventParticipant> participants = new ArrayList<>();
        participants.add(EventParticipant.create(event, host, EventRole.HOST));

        if (participantIds != null && !participantIds.isEmpty()) {
            participantIds.stream()
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

    private void syncParticipants(
            Long eventId,
            Team team,
            List<Long> participantIds
    ) {
        Event targetEvent = getTeamEvent(team.getTeamId(), eventId);
        TeamMember host = eventParticipantRepository.findByEvent_EventId(eventId).stream()
                .filter(participant -> participant.getEventRole() == EventRole.HOST)
                .findFirst()
                .map(EventParticipant::getTeamMember)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.EVENT_PARTICIPANT_HOST_NOT_FOUND));

        eventParticipantRepository.deleteByEvent_EventId(eventId);
        eventParticipantRepository.flush();

        createParticipants(targetEvent, team, host, participantIds);
    }

    private Event getTeamEvent(
            Long teamId,
            Long eventId
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.EVENT_NOT_FOUND));

        if (!teamId.equals(event.getTeamId())) {
            throw new RestApiException(CustomErrorCode.EVENT_TEAM_MISMATCH);
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
            throw new RestApiException(CustomErrorCode.EVENT_PARTICIPANT_INVALID);
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

    private TeamMember validateTeamEventManager(
            Team team,
            User user
    ) {
        TeamMember teamMember = validateTeamMember(team, user);
        if (teamMember.getTeamRole() == TeamRole.LEADER
                || teamMember.getTeamRole() == TeamRole.MANAGER) {
            return teamMember;
        }

        throw new RestApiException(CustomErrorCode.EVENT_FORBIDDEN);
    }
}
