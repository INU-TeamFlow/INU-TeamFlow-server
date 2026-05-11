package com.inuteamflow.server.domain.event.repository;

import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.event.enums.EventKind;
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

    List<Event> findByCreatedByAndTeamIdIsNullAndEventKindAndStartAtBeforeAndEndAtAfter(
            Long createdBy,
            EventKind eventKind,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByCreatedByAndTeamIdIsNullAndEventKindAndStartAtBefore(
            Long createdBy,
            EventKind eventKind,
            LocalDateTime endAt
    );

    List<Event> findByTeamIdAndEventKindAndStartAtBeforeAndEndAtAfter(
            Long teamId,
            EventKind eventKind,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByTeamIdAndEventKindAndStartAtBefore(
            Long teamId,
            EventKind eventKind,
            LocalDateTime endAt
    );
}
