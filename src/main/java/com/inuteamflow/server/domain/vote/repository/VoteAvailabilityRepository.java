package com.inuteamflow.server.domain.vote.repository;

import com.inuteamflow.server.domain.vote.entity.VoteAvailability;
import com.inuteamflow.server.domain.vote.entity.VoteParticipant;
import com.inuteamflow.server.domain.vote.entity.VoteTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteAvailabilityRepository extends JpaRepository<VoteAvailability, Long> {

    List<VoteAvailability> findByVoteParticipant(VoteParticipant voteParticipant);

    List<VoteAvailability> findByVoteTimeSlotIn(List<VoteTimeSlot> voteTimeSlots);

    @Query("""
            select va
            from VoteAvailability va
            where va.voteTimeSlot.voteDate.vote.voteId = :voteId
            """)
    List<VoteAvailability> findByVoteId(@Param("voteId") Long voteId);

    List<VoteAvailability> findByVoteTimeSlot(VoteTimeSlot voteTimeSlot);

    Integer countByVoteTimeSlot(VoteTimeSlot voteTimeSlot);

    void deleteByVoteParticipant(VoteParticipant voteParticipant);

}
