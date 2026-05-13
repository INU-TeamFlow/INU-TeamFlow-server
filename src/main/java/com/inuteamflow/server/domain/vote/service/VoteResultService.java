package com.inuteamflow.server.domain.vote.service;

import com.inuteamflow.server.domain.event.dto.response.EventDetailResponse;
import com.inuteamflow.server.domain.event.entity.Event;
import com.inuteamflow.server.domain.event.entity.EventParticipant;
import com.inuteamflow.server.domain.event.enums.EventRole;
import com.inuteamflow.server.domain.event.repository.EventParticipantRepository;
import com.inuteamflow.server.domain.event.repository.EventRepository;
import com.inuteamflow.server.domain.team.entity.TeamMember;
import com.inuteamflow.server.domain.vote.dto.VoteResultEventCreateCommand;
import com.inuteamflow.server.domain.vote.dto.request.EventVoteTimeSelectRequest;
import com.inuteamflow.server.domain.vote.entity.Vote;
import com.inuteamflow.server.domain.vote.entity.VoteParticipant;
import com.inuteamflow.server.domain.vote.entity.VoteResult;
import com.inuteamflow.server.domain.vote.repository.VoteResultRepository;
import com.inuteamflow.server.global.exception.error.CustomErrorCode;
import com.inuteamflow.server.global.exception.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteResultService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final VoteResultRepository voteResultRepository;

    // 투표 결과를 확정하고 일정과 투표 결과를 생성한다.
    @Transactional
    public EventDetailResponse createVoteResult(
            Vote vote,
            TeamMember host,
            List<VoteParticipant> participants,
            EventVoteTimeSelectRequest request
    ) {
        validateVoteResultCreatable(vote, request);

        Event event = eventRepository.save(Event.create(
                vote.getTeam(),
                new VoteResultEventCreateCommand(vote, request)
        ));
        createEventParticipants(event, vote, host, participants);
        voteResultRepository.save(VoteResult.create(
                vote,
                event,
                request.getIsAllDay(),
                request.getSelectedStartAt(),
                request.getSelectedEndAt()
        ));
        vote.close();

        return EventDetailResponse.create(event, null);
    }

    // 투표 결과 생성 가능 여부를 검증한다.
    private void validateVoteResultCreatable(
            Vote vote,
            EventVoteTimeSelectRequest request
    ) {
        if (voteResultRepository.existsByVote(vote)) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        if (request.getSelectedStartAt() == null || request.getSelectedEndAt() == null) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }

        if (!request.getSelectedStartAt().isBefore(request.getSelectedEndAt())) {
            throw new RestApiException(CustomErrorCode.COMMON_INVALID_REQUEST);
        }
    }

    // TODO: 현재는 모든 투표 참여자를 일정 참여자로 편입하는데, 추후 선택한 시간대에 투표한 사람만 일정 참여자로 편입하는 방식으로 수정
    // 투표 참여자를 일정 참여자로 생성한다.
    private void createEventParticipants(
            Event event,
            Vote vote,
            TeamMember host,
            List<VoteParticipant> participants
    ) {
        List<EventParticipant> eventParticipants = new ArrayList<>();
        eventParticipants.add(EventParticipant.create(event, host, EventRole.HOST));

        for (VoteParticipant participant : participants) {
            TeamMember teamMember = participant.getTeamMember();

            if (teamMember.getTeamMemberId().equals(host.getTeamMemberId())) {
                continue;
            }

            eventParticipants.add(EventParticipant.create(event, teamMember, EventRole.PARTICIPANT));
        }

        eventParticipantRepository.saveAll(eventParticipants);
    }
}
