package com.inuteamflow.server.domain.vote.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVoteTimeSelectRequest {

    private String title;

    @NotNull
    private Boolean isAllDay;

    @NotNull
    private LocalDateTime selectedStartAt;

    @NotNull
    private LocalDateTime selectedEndAt;

    @AssertTrue(message = "selectedStartAt < selectedEndAt")
    public boolean isValidDateTimeRange() {
        if (selectedStartAt == null || selectedEndAt == null) {
            return true;
        }

        return selectedStartAt.isBefore(selectedEndAt);
    }

}
