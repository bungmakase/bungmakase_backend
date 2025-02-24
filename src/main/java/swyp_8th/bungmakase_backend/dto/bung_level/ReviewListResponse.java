package swyp_8th.bungmakase_backend.dto.bung_level;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {

    private UUID reviewId;
    private String profileImageUrl;
    private Long userLevel;
    private String nickname;
    private List<String> bungImages;
    private String reviewText;
    private LocalDateTime reviewTimestamp;
}
