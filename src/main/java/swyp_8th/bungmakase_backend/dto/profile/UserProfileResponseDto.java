package swyp_8th.bungmakase_backend.dto.profile;

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
