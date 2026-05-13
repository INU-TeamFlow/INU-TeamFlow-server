package com.inuteamflow.server.domain.vote.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVoteCreateRequest {

    @NotBlank
    private String title;

    @NotNull
    @Size(min = 1)
    private List<@NotNull Long> participants;

    @NotNull
    private Boolean isAllDay;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @AssertTrue(message = "startDate <= endDate")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }

        return !startDate.isAfter(endDate);
    }

    @AssertTrue(message = "isAllDay가 false일 때, startTime과 endTime은 필수이고 startTime < endTime")
    public boolean isValidTimeRange() {
        if (isAllDay == null || isAllDay) {
            return true;
        }

        if (startTime == null || endTime == null) {
            return false;
        }

        return startTime.isBefore(endTime);
    }

}
