package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bung_shop")
@Getter
@Setter
public class BungShop {

    @Id
    @GeneratedValue
    private UUID id;

    private String shopName;

    private String shopAddress;

    private BigDecimal longitude;
    private BigDecimal latitude;

    private String phoneNumber;
    private String openingHours;
}
