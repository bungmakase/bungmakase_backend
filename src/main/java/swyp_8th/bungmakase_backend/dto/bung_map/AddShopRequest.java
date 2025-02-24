package swyp_8th.bungmakase_backend.dto.bung_map;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AddShopRequest {

    private String shopName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String phone;
    private String startTime;
    private String endTime;
    private List<String> tastes;
}
