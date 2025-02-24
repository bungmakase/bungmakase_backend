package swyp_8th.bungmakase_backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileResponseDto {
    private String nickname; //user nickname
    private Long level; // user level
    private String imageUrl; //profile image URL
}
