package swyp_8th.bungmakase_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bung_shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BungShop {

    @Id
    @GeneratedValue
    private UUID id;

    private String shopName;

    private String shopAddress;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    private String phoneNumber;


    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "tastes", columnDefinition = "TEXT")
    private String tastes; // JSON 또는 콤마 구분 문자열로 저장 가능

    @Column(nullable = false, columnDefinition = "INT DEFAULT 5")
    private Integer star = 5;

    @OneToMany(mappedBy = "bungShop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BungShopReview> bungShopReviews = new ArrayList<>();

    @OneToMany(mappedBy = "bungShop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BungShopImage> bungShopImages = new ArrayList<>();

    // 편의 메서드 - 리뷰 추가
    public void addReview(BungShopReview review) {
        bungShopReviews.add(review);
        review.setBungShop(this);
    }

    // 편의 메서드 - 이미지 추가
    public void addImage(BungShopImage image) {
        bungShopImages.add(image);
        image.setBungShop(this);
    }
}
