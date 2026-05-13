package com.inuteamflow.server.domain.event.controller;

import com.inuteamflow.server.domain.event.dto.request.TeamEventCreateRequest;
import com.inuteamflow.server.domain.event.dto.request.TeamEventUpdateRequest;
import com.inuteamflow.server.domain.event.dto.response.EventDetailResponse;
import com.inuteamflow.server.domain.event.dto.response.EventListResponse;
import com.inuteamflow.server.domain.event.enums.RecurrenceEditScope;
import com.inuteamflow.server.domain.event.service.TeamEventService;
import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import com.inuteamflow.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/events")
public class TeamEventController {

    private final TeamEventService teamEventService;

    @GetMapping
    public ApiResponse<List<EventListResponse>> getTeamEventList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("teamId") Long teamId,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month
    ) {
        return ApiResponse.ok(teamEventService.getTeamEventList(userDetails.getUser(), teamId, year, month));
    }

    @PostMapping
    public ApiResponse<EventDetailResponse> createEvent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody TeamEventCreateRequest request
    ) {
        return ApiResponse.ok(teamEventService.createTeamEvent(userDetails.getUser(), teamId, request));
    }

    @PutMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> updateTeamEvent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("teamId") Long teamId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody TeamEventUpdateRequest request
    ) {
        return ApiResponse.ok(teamEventService.updateTeamEvent(userDetails.getUser(), teamId, eventId, request));
    }

    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteTeamEvent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("teamId") Long teamId,
            @PathVariable("eventId") Long eventId,
            @RequestParam(required = false) RecurrenceEditScope recurrenceEditScope,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime occurrenceAt
    ) {
        teamEventService.deleteTeamEvent(userDetails.getUser(), teamId, eventId, recurrenceEditScope, occurrenceAt);
        return ApiResponse.ok();
    }

}
