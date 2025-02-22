package swyp_8th.bungmakase_backend.dto.bung_dogam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BungResponseDto {
    private String bungId;
    private String bungName;
    private List<String> tags;  // 태그 리스트
}
