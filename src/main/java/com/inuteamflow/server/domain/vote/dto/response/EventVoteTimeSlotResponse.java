package com.inuteamflow.server.domain.vote.dto.response;

import com.inuteamflow.server.domain.vote.entity.VoteTimeSlot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventVoteTimeSlotResponse {

    private Long slotId;
    private LocalDate date;
    private LocalTime startAt;
    private LocalTime endAt;
    private Integer participantCount;

    public static EventVoteTimeSlotResponse create(
            VoteTimeSlot voteTimeSlot,
            Integer participantCount
    ) {
        return new EventVoteTimeSlotResponse(
                voteTimeSlot.getVoteTimeSlotId(),
                voteTimeSlot.getVoteDate().getDate(),
                voteTimeSlot.getSlotStartAt(),
                voteTimeSlot.getSlotEndAt(),
                participantCount
        );
    }
}
