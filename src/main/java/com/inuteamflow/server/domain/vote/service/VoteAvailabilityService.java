package com.inuteamflow.server.domain.vote.service;

import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteAvailability;
import com.inuteamflow.server.domain.vote.entity.VoteParticipant;
import com.inuteamflow.server.domain.vote.entity.VoteTimeSlot;
import com.inuteamflow.server.domain.vote.repository.VoteAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteAvailabilityService {

    private final VoteAvailabilityRepository voteAvailabilityRepository;

    // 참여자의 시간 슬롯 선택 정보를 갱신한다.
    @Transactional
    public void updateVoteAvailabilities(
            VoteParticipant voteParticipant,
            List<VoteTimeSlot> voteTimeSlots
    ) {
        voteAvailabilityRepository.deleteByVoteParticipant(voteParticipant);

        List<VoteAvailability> voteAvailabilities = voteTimeSlots.stream()
                .map(voteTimeSlot -> VoteAvailability.create(voteParticipant, voteTimeSlot))
                .toList();

        voteAvailabilityRepository.saveAll(voteAvailabilities);
    }

    // 시간 슬롯별 선택 인원 수를 집계한다.
    public Map<Long, Integer> countParticipantsByTimeSlot(
            Vote vote
    ) {
        if (vote.getVoteId() == null) {
            return Map.of();
        }

        return voteAvailabilityRepository.findByVoteId(vote.getVoteId()).stream()
                .collect(Collectors.groupingBy(
                        voteAvailability -> voteAvailability.getVoteTimeSlot().getVoteTimeSlotId(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    // 특정 시간 슬롯의 선택 인원 수를 조회한다.
    public Integer countParticipants(
            VoteTimeSlot voteTimeSlot
    ) {
        return voteAvailabilityRepository.countByVoteTimeSlot(voteTimeSlot);
    }
}
