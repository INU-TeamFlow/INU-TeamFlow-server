package com.inuteamflow.server.domain.vote.service;

import com.inuteamflow.server.domain.team.entity.TeamMember;
import com.inuteamflow.server.domain.team.repository.TeamMemberRepository;
import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteParticipant;
import com.inuteamflow.server.domain.vote.repository.VoteParticipantRepository;
import com.inuteamflow.server.global.exception.error.CustomErrorCode;
import com.inuteamflow.server.global.exception.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteParticipantService {

    private final VoteParticipantRepository voteParticipantRepository;
    private final TeamMemberRepository teamMemberRepository;

    // 투표 참여자 목록을 생성한다.
    @Transactional
    public void createVoteParticipants(
            Vote vote,
            List<Long> teamMemberIds
    ) {
        if (teamMemberIds == null || teamMemberIds.isEmpty()) {
            return;
        }

        List<VoteParticipant> participants = new ArrayList<>();
        Set<Long> uniqueTeamMemberIds = new HashSet<>();

        for (Long teamMemberId : teamMemberIds) {
            if (teamMemberId == null || !uniqueTeamMemberIds.add(teamMemberId)) {
                continue;
            }

            participants.add(createVoteParticipant(vote, teamMemberId));
        }

        voteParticipantRepository.saveAll(participants);
    }

    // 투표 참여자를 조회한다.
    public VoteParticipant getVoteParticipant(
            Vote vote,
            TeamMember teamMember
    ) {
        return voteParticipantRepository.findByVoteAndTeamMember(vote, teamMember)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.VOTE_PARTICIPANT_NOT_FOUND));
    }

    // 투표를 완료한 참여자 이름 목록을 조회한다.
    public VoteParticipantNames getVoteParticipantNames(
            Vote vote
    ) {
        List<String> completedVoterNames = new ArrayList<>();
        List<String> uncompletedVoterNames = new ArrayList<>();

        for (VoteParticipant voteParticipant : voteParticipantRepository.findByVote(vote)) {
            String voterName = voteParticipant.getTeamMember().getUser().getName();

            if (Boolean.TRUE.equals(voteParticipant.getHasCompleted())) {
                completedVoterNames.add(voterName);
                continue;
            }

            uncompletedVoterNames.add(voterName);
        }

        return new VoteParticipantNames(completedVoterNames, uncompletedVoterNames);
    }

    // 참여자 사용자 정보를 조회하고 투표 참여자를 생성한다.
    private VoteParticipant createVoteParticipant(
            Vote vote,
            Long teamMemberId
    ) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.TEAM_MEMBER_NOT_FOUND));

        if (!vote.getTeam().getTeamId().equals(teamMember.getTeam().getTeamId())) {
            throw new RestApiException(CustomErrorCode.VOTE_PARTICIPANT_NOT_FOUND);
        }

        return VoteParticipant.create(vote, teamMember);
    }

    public record VoteParticipantNames(
            List<String> completedVoterNames,
            List<String> uncompletedVoterNames
    ) {
    }
}
