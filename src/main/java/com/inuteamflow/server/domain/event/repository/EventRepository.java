package com.inuteamflow.server.domain.event.repository;

import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventIdInAndIsSingleAndStartAtBeforeAndEndAtAfter(
            Collection<Long> eventIds,
            Boolean isSingle,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByEventIdInAndIsSingleAndStartAtBefore(
            Collection<Long> eventIds,
            Boolean isSingle,
            LocalDateTime endAt
    );

    List<Event> findByCreatedByAndTeamIsNullAndIsSingleAndStartAtBeforeAndEndAtAfter(
            Long createdBy,
            Boolean isSingle,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByCreatedByAndTeamIsNullAndIsSingleAndStartAtBefore(
            Long createdBy,
            Boolean isSingle,
            LocalDateTime endAt
    );

    List<Event> findByTeamAndIsSingleAndStartAtBeforeAndEndAtAfter(
            Team team,
            Boolean isSingle,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    List<Event> findByTeamAndIsSingleAndStartAtBefore(
            Team team,
            Boolean isSingle,
            LocalDateTime endAt
    );
}
