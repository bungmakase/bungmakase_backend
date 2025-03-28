package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_bung_log")
@Getter
@Setter
public class UserBungLog {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "bung_id")
    private BungDogam bung;

    private LocalDate logDate = LocalDate.now();

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long count = 0L;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @OneToMany(mappedBy = "userBungLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBungImage> userBungImages = new ArrayList<>();
}
