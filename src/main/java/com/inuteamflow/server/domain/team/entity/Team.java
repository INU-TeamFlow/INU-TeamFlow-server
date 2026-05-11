package com.inuteamflow.server.domain.team.entity;

import com.inuteamflow.server.domain.team.dto.request.TeamCreateRequest;
import com.inuteamflow.server.domain.team.dto.request.TeamUpdateRequest;
import com.inuteamflow.server.domain.team.enums.TeamCategory;
import com.inuteamflow.server.domain.user.entity.User;
import com.inuteamflow.server.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private TeamCategory category;

    private String link;

    private String sns;

    @Column(name = "image_key")
    private String imageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Builder
    private Team (String name, String description, TeamCategory category,
                  String link, String sns, String imageKey, User createdBy) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.link = link;
        this.sns = sns;
        this.imageKey = imageKey;
        this.createdBy = createdBy;
    }

    public static Team create(TeamCreateRequest request, User creator) {
        return Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .link(request.getLink())
                .sns(request.getSns())
                .imageKey(request.getImageKey())
                .createdBy(creator)
                .build();
    }

    public void update(TeamUpdateRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.category = request.getCategory();
        this.link = request.getLink();
        this.sns = request.getSns();
        this.imageKey = request.getImageKey();
    }
}
