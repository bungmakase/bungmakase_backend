package swyp_8th.bungmakase_backend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class UpdateLogResponseDto {
    private String recordId;      // UserBungLog의 id (문자열)
    private String bungName;      // 붕어빵 이름 (BungDogam.bungName)
    private int bungCount;        // 붕어빵 개수
    private List<String> tags;    // 태그 목록
    private List<String> imageUrls; // 해당 기록에 첨부된 이미지 URL 목록
    private LocalDate date;       // 기록 날짜 (logDate의 날짜 부분)
}
