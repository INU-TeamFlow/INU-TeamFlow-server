package com.inuteamflow.server.domain.event.controller;

import com.inuteamflow.server.domain.event.dto.request.MyEventCreateRequest;
import com.inuteamflow.server.domain.event.dto.request.MyEventUpdateRequest;
import com.inuteamflow.server.domain.event.dto.response.EventDetailResponse;
import com.inuteamflow.server.domain.event.dto.response.EventListResponse;
import com.inuteamflow.server.domain.event.enums.RecurrenceEditScope;
import com.inuteamflow.server.domain.event.service.MyEventService;
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
@RequestMapping("/api/v1/events")
public class MyEventController {

    private final MyEventService myEventService;

    @GetMapping
    public ApiResponse<List<EventListResponse>> getMyEventList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ApiResponse.ok(myEventService.getMyEventList(userDetails.getUser(), year, month));
    }

    @PostMapping
    public ApiResponse<EventDetailResponse> createMyEvent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody MyEventCreateRequest request
    ) {
        return ApiResponse.ok(myEventService.createMyEvent(userDetails.getUser(), request));
    }

    @PutMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> updateMyEvent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long eventId,
            @Valid @RequestBody MyEventUpdateRequest request
    ) {
        return ApiResponse.ok(myEventService.updateMyEvent(userDetails.getUser(), eventId, request));
    }

    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteMyEvent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long eventId,
            @RequestParam(required = false) RecurrenceEditScope recurrenceEditScope,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime occurrenceAt
    ) {
        myEventService.deleteMyEvent(userDetails.getUser(), eventId, recurrenceEditScope, occurrenceAt);
        return ApiResponse.ok();
    }

}
