package com.inuteamflow.server.domain.event.dto.request;

import com.inuteamflow.server.domain.event.dto.Recurrence;
import com.inuteamflow.server.domain.event.dto.EventUpdateCommand;
import com.inuteamflow.server.domain.event.enums.EventColor;
import com.inuteamflow.server.domain.event.enums.RecurrenceEditScope;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamEventUpdateRequest implements EventUpdateCommand {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @NotNull
    private Boolean isAllDay;

    @NotNull
    private EventColor color;

    private Boolean isFinished;

    private List<@NotNull Long> participants;

    private RecurrenceEditScope recurrenceEditScope;

    private LocalDateTime occurrenceAt;

    @Valid
    private Recurrence recurrence;

    @AssertTrue(message = "startAt must be before endAt")
    public boolean isValidDateRange() {
        if (startAt == null || endAt == null) {
            return true;
        }

        return startAt.isBefore(endAt);
    }

    @AssertTrue(message = "occurrenceAt is required for THIS_INSTANCE or THIS_AND_FOLLOWING")
    public boolean isValidOccurrenceAt() {
        if (recurrenceEditScope == RecurrenceEditScope.THIS_INSTANCE
                || recurrenceEditScope == RecurrenceEditScope.THIS_AND_FOLLOWING) {
            return occurrenceAt != null;
        }

        return true;
    }
}
