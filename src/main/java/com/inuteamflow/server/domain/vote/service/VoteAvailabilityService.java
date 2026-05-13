package com.inuteamflow.server.domain.vote.service;

import com.inuteamflow.server.domain.team.entity.TeamMember;
import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteAvailability;
import com.inuteamflow.server.domain.vote.entity.VoteParticipant;
import com.inuteamflow.server.domain.vote.entity.VoteTimeSlot;
import com.inuteamflow.server.domain.vote.repository.VoteAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        voteAvailabilityRepository.flush();

        List<VoteAvailability> voteAvailabilities = new ArrayList<>();

        for (VoteTimeSlot voteTimeSlot : voteTimeSlots) {
            voteAvailabilities.add(VoteAvailability.create(voteParticipant, voteTimeSlot));
        }

        voteAvailabilityRepository.saveAll(voteAvailabilities);
    }

    // 시간 슬롯별 선택 인원 수를 집계한다.
    public Map<Long, Integer> countParticipantsByTimeSlot(
            Vote vote
    ) {
        if (vote.getVoteId() == null) {
            return Map.of();
        }

        Map<Long, Integer> participantCountByTimeSlot = new HashMap<>();

        for (VoteAvailability voteAvailability : voteAvailabilityRepository.findByVoteId(vote.getVoteId())) {
            Long voteTimeSlotId = voteAvailability.getVoteTimeSlot().getVoteTimeSlotId();
            participantCountByTimeSlot.put(
                    voteTimeSlotId,
                    participantCountByTimeSlot.getOrDefault(voteTimeSlotId, 0) + 1
            );
        }

        return participantCountByTimeSlot;
    }

    // 특정 시간 슬롯의 선택 인원 수를 조회한다.
    public Integer countParticipants(
            VoteTimeSlot voteTimeSlot
    ) {
        return voteAvailabilityRepository.countByVoteTimeSlot(voteTimeSlot);
    }

    // 특정 시간칸에 가능한 참여자 목록을 조회한다.
    public List<TeamMember> getAvailableTeamMembers(
            List<VoteTimeSlot> voteTimeSlots
    ) {
        if (voteTimeSlots == null || voteTimeSlots.isEmpty()) {
            return List.of();
        }

        List<VoteAvailability> voteAvailabilities = voteAvailabilityRepository.findByVoteTimeSlotIn(voteTimeSlots);
        List<TeamMember> teamMembers = new ArrayList<>();
        Map<Long, TeamMember> teamMemberById = new HashMap<>();
        Map<Long, Integer> selectedSlotCountByTeamMemberId = new HashMap<>();

        for (VoteAvailability voteAvailability : voteAvailabilities) {
            TeamMember teamMember = voteAvailability.getVoteParticipant().getTeamMember();
            Long teamMemberId = teamMember.getTeamMemberId();

            teamMemberById.put(teamMemberId, teamMember);
            selectedSlotCountByTeamMemberId.put(
                    teamMemberId,
                    selectedSlotCountByTeamMemberId.getOrDefault(teamMemberId, 0) + 1
            );
        }

        for (Long teamMemberId : selectedSlotCountByTeamMemberId.keySet()) {
            if (selectedSlotCountByTeamMemberId.get(teamMemberId) == voteTimeSlots.size()) {
                teamMembers.add(teamMemberById.get(teamMemberId));
            }
        }

        return teamMembers;
    }
}
