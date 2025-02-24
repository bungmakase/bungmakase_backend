package swyp_8th.bungmakase_backend.dto.bung_map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopPhotoResponse {

    private String photoId;
    private String imageUrl;
    private LocalDateTime uploadedAt;
}
