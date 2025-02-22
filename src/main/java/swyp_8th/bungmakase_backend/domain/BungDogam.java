package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bung_dogam")
@Getter
@Setter
public class BungDogam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(nullable = false)
    private String bungName;

    @OneToMany(mappedBy = "bung", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBungDogam> userBungDogams = new ArrayList<>();
}
