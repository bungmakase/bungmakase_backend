package swyp_8th.bungmakase_backend.dto.bung_map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarkerResponseDto {
    private String shopId;
    private String shopName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer star;
    private String startTime;
    private String endTime;
    private List<String> tastes;
}