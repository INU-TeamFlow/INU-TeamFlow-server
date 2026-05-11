package com.inuteamflow.server.domain.event.dto.request;

import com.inuteamflow.server.domain.event.dto.Recurrence;
import com.inuteamflow.server.domain.event.dto.EventCreateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyEventCreateRequest implements EventCreateCommand {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @NotNull
    private Boolean isAllDay;

    private String color;

    @Valid
    private Recurrence recurrence;

    @AssertTrue(message = "startAt must be before endAt")
    public boolean isValidDateRange() {
        if (startAt == null || endAt == null) {
            return true;
        }

        return startAt.isBefore(endAt);
    }
}
