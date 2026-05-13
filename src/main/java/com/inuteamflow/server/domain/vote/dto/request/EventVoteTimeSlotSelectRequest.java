package com.inuteamflow.server.domain.vote.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVoteTimeSlotSelectRequest {

    @NotNull
    @Size(min = 1)
    private List<@NotNull Long> slotIdList;

}
