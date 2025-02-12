package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_bung_images")
@Getter
@Setter
public class UserBungImage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_bung_log_id")
    private UserBungLog userBungLog;

    @Column(nullable = false)
    private String imageUrl;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
