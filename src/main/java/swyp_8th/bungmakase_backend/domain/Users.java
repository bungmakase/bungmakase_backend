package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swyp_8th.bungmakase_backend.domain.enums.UserAuthTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String nickname;

    private String image_url;

    private String email; // 이메일 로그인 시 필요

    @Enumerated(EnumType.STRING)
    private UserAuthTypeEnum authType;

    @Column(unique = true)
    private String oauthId; // 카카오 로그인 시 필요

    private String password; // 이메일 로그인 시 필요

    private String guestId; // 게스트 로그인 시 필요

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long level = 1L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long bungCount = 0L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long recentCount = 0L;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
