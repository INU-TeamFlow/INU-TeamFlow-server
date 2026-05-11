package com.inuteamflow.server.domain.event.repository;

import com.inuteamflow.server.domain.event.entity.RecurrenceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RecurrenceRuleRepository extends JpaRepository<RecurrenceRule, Long> {

    List<RecurrenceRule> findByEventIdIn(Collection<Long> eventIds);

    Optional<RecurrenceRule> findByEventId(Long eventId);

    void deleteByEventId(Long eventId);
}
