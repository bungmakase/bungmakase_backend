package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bung_shop_images")
@Getter
@Setter
public class BungShopImage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bung_shop_id")
    private BungShop bungShop;

    private String imageUrl;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
