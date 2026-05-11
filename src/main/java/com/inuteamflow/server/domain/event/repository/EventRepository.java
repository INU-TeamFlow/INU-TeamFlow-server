package com.inuteamflow.server.domain.event.repository;

import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.event.enums.EventKind;
import com.inuteamflow.server.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventIdInAndEventKindAndStartAtBeforeAndEndAtAfter(
            Collection<Long> eventIds,
            EventKind eventKind,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByEventIdInAndEventKindAndStartAtBefore(
            Collection<Long> eventIds,
            EventKind eventKind,
            LocalDateTime endAt
    );

    List<Event> findByCreatedByAndTeamIsNullAndEventKindAndStartAtBeforeAndEndAtAfter(
            Long createdBy,
            EventKind eventKind,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByCreatedByAndTeamIsNullAndEventKindAndStartAtBefore(
            Long createdBy,
            EventKind eventKind,
            LocalDateTime endAt
    );

    List<Event> findByTeamAndEventKindAndStartAtBeforeAndEndAtAfter(
            Team team,
            EventKind eventKind,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByTeamAndEventKindAndStartAtBefore(
            Team team,
            EventKind eventKind,
            LocalDateTime endAt
    );
}
