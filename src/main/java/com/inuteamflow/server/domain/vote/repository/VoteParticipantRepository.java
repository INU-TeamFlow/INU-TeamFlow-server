package com.inuteamflow.server.domain.vote.repository;

import com.inuteamflow.server.domain.team.entity.TeamMember;
import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteParticipantRepository extends JpaRepository<VoteParticipant, Long> {

    List<VoteParticipant> findByVote(Vote vote);

    Optional<VoteParticipant> findByVoteAndTeamMember(Vote vote, TeamMember teamMember);

}
