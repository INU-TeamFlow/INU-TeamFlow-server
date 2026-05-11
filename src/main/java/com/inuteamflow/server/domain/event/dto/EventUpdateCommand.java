package com.inuteamflow.server.domain.event.dto;

import com.inuteamflow.server.domain.event.enums.EventStatus;
import com.inuteamflow.server.domain.event.enums.RecurrenceEditScope;

import java.time.LocalDateTime;

public interface EventUpdateCommand extends EventCreateCommand {

    EventStatus getStatus();

    RecurrenceEditScope getRecurrenceEditScope();

    LocalDateTime getOccurrenceAt();
}
