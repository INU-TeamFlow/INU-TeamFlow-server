package com.inuteamflow.server.domain.vote.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVoteTimeSelectRequest {

    private String title;
    private Boolean isAllDay;
    private LocalDateTime selectedStartAt;
    private LocalDateTime selectedEndAt;

}
