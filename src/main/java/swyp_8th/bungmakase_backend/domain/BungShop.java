package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
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


    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "tastes", columnDefinition = "TEXT")
    private String tastes; // JSON 또는 콤마 구분 문자열로 저장 가능
}
