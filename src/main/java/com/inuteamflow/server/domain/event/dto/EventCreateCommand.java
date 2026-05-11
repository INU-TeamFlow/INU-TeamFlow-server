package com.inuteamflow.server.domain.event.dto;

import java.time.LocalDateTime;

public interface EventCreateCommand {

    String getTitle();

    String getDescription();

    LocalDateTime getStartAt();

    LocalDateTime getEndAt();

    Boolean getIsAllDay();

    String getColor();

    Recurrence getRecurrence();
}
