package com.inuteamflow.server.global.jwt.refresh;

import com.inuteamflow.server.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true, length = 500)
    private String refreshToken;

    @Builder
    public RefreshToken(User user, String refreshToken) {
        this.userId = user.getUserId();
        this.refreshToken = refreshToken;
    }

    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
