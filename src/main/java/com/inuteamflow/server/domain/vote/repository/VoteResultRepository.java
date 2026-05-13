package com.inuteamflow.server.domain.vote.repository;

import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {

    boolean existsByVote(Vote vote);

}
