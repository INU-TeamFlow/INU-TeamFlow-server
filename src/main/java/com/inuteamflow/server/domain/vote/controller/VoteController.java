package com.inuteamflow.server.domain.vote.controller;

import com.inuteamflow.server.domain.event.dto.response.EventDetailResponse;
import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import com.inuteamflow.server.domain.vote.dto.request.EventVoteCreateRequest;
import com.inuteamflow.server.domain.vote.dto.request.EventVoteTimeSelectRequest;
import com.inuteamflow.server.domain.vote.dto.request.EventVoteTimeSlotSelectRequest;
import com.inuteamflow.server.domain.vote.dto.response.EventVoteResponse;
import com.inuteamflow.server.domain.vote.dto.response.EventVoteTimeSlotResponse;
import com.inuteamflow.server.domain.vote.service.VoteService;
import com.inuteamflow.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/teams/{teamId}/votes")
    public ApiResponse<List<EventVoteResponse>> getVoteList(
            @PathVariable("teamId") Long teamId
    ) {
        return ApiResponse.ok(voteService.getVoteList(teamId));
    }

    @PostMapping("/teams/{teamId}/votes")
    public ApiResponse<EventVoteResponse> createVote(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody EventVoteCreateRequest request
    ) {
        return ApiResponse.ok(voteService.createVote(userDetails, teamId, request));
    }

    @GetMapping("/votes/{voteId}")
    public ApiResponse<EventVoteResponse> getVote(
            @PathVariable("voteId") Long voteId
    ) {
        return ApiResponse.ok(voteService.getVote(voteId));
    }

    @GetMapping("/votes/{voteId}/slots")
    public ApiResponse<List<EventVoteTimeSlotResponse>> getTimeSlot(
            @PathVariable("voteId") Long voteId
    ) {
        return ApiResponse.ok(voteService.getTimeSlot(voteId));
    }

    @PutMapping("/votes/{voteId}/slots")
    public ApiResponse<EventVoteTimeSlotResponse> selectTimeSlot(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("voteId") Long voteId,
            @Valid @RequestBody EventVoteTimeSlotSelectRequest request
    ) {
        return ApiResponse.ok(voteService.selectTimeSlot(userDetails, voteId, request));
    }

    @PostMapping("/votes/{voteId}/result")
    public ApiResponse<EventDetailResponse> createVoteResult(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("voteId") Long voteId,
            @Valid @RequestBody EventVoteTimeSelectRequest request
    ) {
        return ApiResponse.ok(voteService.createVoteResult(userDetails, voteId, request));
    }

}
