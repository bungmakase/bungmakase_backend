package swyp_8th.bungmakase_backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingResponseDto {
    private int rank; // user ranking start from 1
    private String nickname; // user nickname
    private Long level; // user level
    private Long recentBungCount; // total fish-bun that user ate
}
