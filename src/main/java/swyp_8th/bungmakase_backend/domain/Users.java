package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.*;
import swyp_8th.bungmakase_backend.domain.enums.UserAuthTypeEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@Table(name = "users")
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
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

    @Column(columnDefinition = "BIGINT DEFAULT 1")
    private Long level = 1L;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long bungCount = 0L;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long recentCount = 0L;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true) // ✅ 추가
    private GuestSession guestSession;

}
