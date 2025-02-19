package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "guest_sessions")
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class GuestSession {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDateTime expiresAt;
}
