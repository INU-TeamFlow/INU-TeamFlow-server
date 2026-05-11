package com.inuteamflow.server.domain.event.repository;

import com.inuteamflow.server.domain.event.entity.RecurrenceException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RecurrenceExceptionRepository extends JpaRepository<RecurrenceException, Long> {

    List<RecurrenceException> findByEventIdIn(Collection<Long> eventIds);

    Optional<RecurrenceException> findByEventIdAndOriginalOccurrenceAt(
            Long eventId,
            LocalDateTime originalOccurrenceAt
    );

    void deleteByEventId(Long eventId);

    void deleteByEventIdAndOriginalOccurrenceAtGreaterThanEqual(
            Long eventId,
            LocalDateTime originalOccurrenceAt
    );
}
