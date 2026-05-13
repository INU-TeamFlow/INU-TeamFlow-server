package com.inuteamflow.server.domain.vote.repository;

import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteDateRepository extends JpaRepository<VoteDate, Long> {

    List<VoteDate> findByVoteOrderByDateAsc(Vote vote);

}
