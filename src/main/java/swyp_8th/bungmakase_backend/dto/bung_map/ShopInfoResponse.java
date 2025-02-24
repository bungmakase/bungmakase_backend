package swyp_8th.bungmakase_backend.dto.bung_map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopInfoResponse {

    private String shopId;
    private String shopName;
    private String startTime;
    private String endTime;
    private String address;
    private List<String> tastes;
    private String phone;
    private List<String> imageUrls;
}
