package com.inuteamflow.server.domain.vote.repository;

import com.inuteamflow.server.domain.vote.entity.VoteDate;
import com.inuteamflow.server.domain.vote.entity.VoteTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteTimeSlotRepository extends JpaRepository<VoteTimeSlot, Long> {

    @Query("""
            select vts
            from VoteTimeSlot vts
            where vts.voteDate in :voteDates
            order by vts.voteDate.date asc, vts.slotStartAt asc
            """)
    List<VoteTimeSlot> findByVoteDatesOrderByDateAndStartAt(@Param("voteDates") List<VoteDate> voteDates);
}
