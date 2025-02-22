package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_bung_dogam")
@Getter
@Setter
public class UserBungDogam {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "bung_id")
    private BungDogam bung;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean found = false;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;
}
