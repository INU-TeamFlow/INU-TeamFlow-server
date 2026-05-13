package com.inuteamflow.server.domain.vote.service;

import com.inuteamflow.server.domain.vote.dto.request.EventVoteCreateRequest;
import com.inuteamflow.server.domain.vote.dto.response.EventVoteTimeSlotResponse;
import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteDate;
import com.inuteamflow.server.domain.vote.entity.VoteTimeSlot;
import com.inuteamflow.server.domain.vote.repository.VoteDateRepository;
import com.inuteamflow.server.domain.vote.repository.VoteTimeSlotRepository;
import com.inuteamflow.server.global.exception.error.CustomErrorCode;
import com.inuteamflow.server.global.exception.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteTimeService {

    private final VoteDateRepository voteDateRepository;
    private final VoteTimeSlotRepository voteTimeSlotRepository;

    // 투표 가능한 날짜 목록을 생성한다.
    @Transactional
    public List<VoteDate> createVoteDates(
            Vote vote,
            EventVoteCreateRequest request
    ) {
        validateDateRange(request);

        List<VoteDate> voteDates = new ArrayList<>();
        LocalDate date = request.getStartDate();

        while (!date.isAfter(request.getEndDate())) {
            voteDates.add(VoteDate.create(vote, date));
            date = date.plusDays(1);
        }

        return voteDateRepository.saveAll(voteDates);
    }

    // 투표 날짜별 시간 슬롯을 생성한다.
    @Transactional
    public void createVoteTimeSlots(
            List<VoteDate> voteDates,
            EventVoteCreateRequest request
    ) {
        validateTimeRange(request);

        List<VoteTimeSlot> voteTimeSlots = voteDates.stream()
                .flatMap(voteDate -> createVoteTimeSlots(voteDate, request).stream())
                .toList();

        voteTimeSlotRepository.saveAll(voteTimeSlots);
    }

    // 투표 날짜 목록을 조회한다.
    public List<VoteDate> getVoteDates(
            Vote vote
    ) {
        return voteDateRepository.findByVoteOrderByDateAsc(vote);
    }

    // 투표 시간 슬롯 목록을 조회한다.
    public List<VoteTimeSlot> getVoteTimeSlots(
            Vote vote
    ) {
        List<VoteDate> voteDates = getVoteDates(vote);

        if (voteDates.isEmpty()) {
            return List.of();
        }

        return voteTimeSlotRepository.findByVoteDatesOrderByDateAndStartAt(voteDates);
    }

    // 시간 슬롯 ID 목록으로 시간 슬롯을 조회한다.
    public List<VoteTimeSlot> getVoteTimeSlotsByIds(
            List<Long> voteTimeSlotIds
    ) {
        if (voteTimeSlotIds == null || voteTimeSlotIds.isEmpty()) {
            return List.of();
        }

        return voteTimeSlotRepository.findAllById(voteTimeSlotIds);
    }

    // 선택 가능한 유효한 시간 슬롯 목록을 조회한다.
    public List<VoteTimeSlot> getValidVoteTimeSlots(
            Vote vote,
            List<Long> voteTimeSlotIds
    ) {
        List<VoteTimeSlot> voteTimeSlots = getVoteTimeSlotsByIds(voteTimeSlotIds);
        validateSelectedVoteTimeSlots(voteTimeSlotIds, voteTimeSlots);
        validateVoteTimeSlots(vote, voteTimeSlots);

        return voteTimeSlots;
    }

    // 시간 슬롯 응답 객체를 생성한다.
    public List<EventVoteTimeSlotResponse> createVoteTimeSlotResponses(
            List<VoteTimeSlot> voteTimeSlots,
            Map<Long, Integer> participantCountByTimeSlot
    ) {
        return voteTimeSlots.stream()
                .map(voteTimeSlot -> EventVoteTimeSlotResponse.create(
                        voteTimeSlot,
                        participantCountByTimeSlot.getOrDefault(voteTimeSlot.getVoteTimeSlotId(), 0)
                ))
                .toList();
    }

    // 시간 슬롯들이 해당 투표에 속하는지 검증한다.
    public void validateVoteTimeSlots(
            Vote vote,
            List<VoteTimeSlot> voteTimeSlots
    ) {
        if (voteTimeSlots == null || voteTimeSlots.isEmpty()) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        boolean hasInvalidVoteTimeSlot = voteTimeSlots.stream()
                .anyMatch(voteTimeSlot -> !vote.getVoteId().equals(
                        voteTimeSlot.getVoteDate().getVote().getVoteId()
                ));

        if (hasInvalidVoteTimeSlot) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }
    }

    // 선택한 시간 슬롯 ID 목록의 유효성을 검증한다.
    private void validateSelectedVoteTimeSlots(
            List<Long> voteTimeSlotIds,
            List<VoteTimeSlot> voteTimeSlots
    ) {
        if (voteTimeSlotIds == null) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        long requestedVoteTimeSlotCount = voteTimeSlotIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .count();

        if (requestedVoteTimeSlotCount == 0 || requestedVoteTimeSlotCount != voteTimeSlots.size()) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }
    }

    // 특정 날짜의 시간 슬롯 목록을 생성한다.
    private List<VoteTimeSlot> createVoteTimeSlots(
            VoteDate voteDate,
            EventVoteCreateRequest request
    ) {
        if (Boolean.TRUE.equals(request.getIsAllDay())) {
            return List.of(VoteTimeSlot.create(voteDate, LocalTime.MIN, LocalTime.MAX));
        }

        List<VoteTimeSlot> voteTimeSlots = new ArrayList<>();
        LocalTime slotStartAt = request.getStartTime();

        while (slotStartAt.isBefore(request.getEndTime())) {
            LocalTime slotEndAt = slotStartAt.plusMinutes(voteDate.getVote().getSlotUnitMinute());

            if (slotEndAt.isAfter(request.getEndTime())) break;

            voteTimeSlots.add(VoteTimeSlot.create(voteDate, slotStartAt, slotEndAt));
            slotStartAt = slotEndAt;
        }

        return voteTimeSlots;
    }

    // 투표 날짜 범위가 유효한지 검증한다.
    private void validateDateRange(
            EventVoteCreateRequest request
    ) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }
    }

    // 투표 시간 범위가 유효한지 검증한다.
    private void validateTimeRange(
            EventVoteCreateRequest request
    ) {
        if (Boolean.TRUE.equals(request.getIsAllDay())) {
            return;
        }

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }
    }
}
