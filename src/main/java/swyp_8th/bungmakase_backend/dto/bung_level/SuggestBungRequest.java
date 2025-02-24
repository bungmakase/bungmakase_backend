package swyp_8th.bungmakase_backend.dto.bung_level;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestBungRequest {

    @NotBlank(message = "붕어빵 이름은 필수 입력값입니다.")
    private String bungName;

    private List<String> tags;
}
