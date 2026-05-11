package com.inuteamflow.server.domain.event.repository;

import com.inuteamflow.server.domain.event.entity.RecurrenceException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RecurrenceExceptionRepository extends JpaRepository<RecurrenceException, Long> {

    List<RecurrenceException> findByEvent_EventIdIn(Collection<Long> eventIds);

    Optional<RecurrenceException> findByEvent_EventIdAndOriginalOccurrenceAt(
            Long eventId,
            LocalDateTime originalOccurrenceAt
    );

    void deleteByEvent_EventId(Long eventId);

    void deleteByEvent_EventIdAndOriginalOccurrenceAtGreaterThanEqual(
            Long eventId,
            LocalDateTime originalOccurrenceAt
    );
}
