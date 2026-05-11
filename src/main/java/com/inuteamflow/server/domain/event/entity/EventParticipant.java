package com.inuteamflow.server.domain.event.entity;

import com.inuteamflow.server.domain.event.enums.EventRole;
import com.inuteamflow.server.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "event_participant",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"event_id", "team_member_id"}
        ))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventParticipant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_participant_id")
    private Long eventParticipantId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "team_member_id", nullable = false)
    private Long teamMemberId;

    @Column(name = "event_role")
    @Enumerated(EnumType.STRING)
    private EventRole eventRole;

    @Builder
    private EventParticipant(
            Long eventId,
            Long teamMemberId,
            EventRole eventRole
    ) {
        this.eventId = eventId;
        this.teamMemberId = teamMemberId;
        this.eventRole = eventRole;
    }

    public static EventParticipant create(
            Long eventId,
            Long teamMemberId,
            EventRole eventRole
    ) {
        return EventParticipant.builder()
                .eventId(eventId)
                .teamMemberId(teamMemberId)
                .eventRole(eventRole)
                .build();
    }
}
