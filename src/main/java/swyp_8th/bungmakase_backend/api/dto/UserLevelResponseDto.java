package swyp_8th.bungmakase_backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLevelResponseDto {
    private String nickname; // user nickname
    private Long level;      // user level
    private Long bungCount;  // amount of fish-bun that user eat
}
