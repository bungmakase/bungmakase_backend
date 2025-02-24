package swyp_8th.bungmakase_backend.dto.bung_map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopReviewRequest {

    private String shopId;
    private Integer star;
    private String bungName;
    private String reviewText;
}