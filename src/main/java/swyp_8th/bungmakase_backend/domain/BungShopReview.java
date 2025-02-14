package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bung_shop_reviews")
@Getter
@Setter
public class BungShopReview {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bung_shop_id")
    private BungShop bungShop;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false)
    private String bungName;

    @Column(nullable = false)
    private String reviewText;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 5")
    private Integer star = 5;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
