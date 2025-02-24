package swyp_8th.bungmakase_backend.dto.bung_level;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BungLogRequestDto {

    private Long bungCount;         // 붕어빵 개수
    private String bungName;       // 붕어빵 이름
    private List<String> tags;     // 태그 리스트
}
