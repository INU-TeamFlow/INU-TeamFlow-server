package com.inuteamflow.server.domain.event.repository;

import com.inuteamflow.server.domain.event.entity.EventParticipant;
import com.inuteamflow.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    List<EventParticipant> findByEvent_EventId(Long eventId);

    List<EventParticipant> findByTeamMember_User(User user);

    void deleteByEvent_EventId(Long eventId);
}
