package com.inuteamflow.server.domain.vote.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVoteTimeSlotSelectRequest {

    private List<Long> slotIdList;

}
