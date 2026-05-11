package com.inuteamflow.server.domain.team.repository;

import com.inuteamflow.server.domain.team.entity.Team;
import com.inuteamflow.server.domain.team.entity.TeamMember;
import com.inuteamflow.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    int countByTeam(Team team);
    List<TeamMember> findByUser(User user);
    List<TeamMember> findByTeam(Team team);
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    void deleteAllByTeam(Team team);
}
