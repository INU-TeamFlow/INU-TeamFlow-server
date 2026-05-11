package com.inuteamflow.server.domain.team.dto.response;

import com.inuteamflow.server.domain.team.entity.TeamMember;
import com.inuteamflow.server.domain.team.enums.TeamRole;
import com.inuteamflow.server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberResponse {

    private Long teamMemberId;

    private Long userId;

    private String username;

    private TeamRole teamRole;

    public static TeamMemberResponse create(TeamMember teamMember, User user) {
        return new TeamMemberResponse(
                teamMember.getTeamMemberId(),
                user.getUserId(),
                user.getUsername(),
                teamMember.getTeamRole()
        );
    }
}
