package swyp_8th.bungmakase_backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.dto.profile.RankingResponseDto;
import swyp_8th.bungmakase_backend.dto.bung_level.UserLevelResponseDto;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.BungLevelService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/level")
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
public class BungLevelController {

    private final BungLevelService bungLevelService;
    private final JwtConfig jwtConfig;


    @GetMapping("/user")
    public ResponseEntity<ResponseTemplate<UserLevelResponseDto>> getUser(
            @CookieValue(value = "token") String token) {

        // 토큰에서 유저 ID 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);

        try {
            UserLevelResponseDto userLevelResponseDto = bungLevelService.getUserLevel(userId);
            ResponseTemplate<UserLevelResponseDto> response = new ResponseTemplate<>(SuccessCode.SUCCESS_200, userLevelResponseDto);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            ResponseTemplate<UserLevelResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);

        }

    }

    @GetMapping("/rankings")
    public ResponseEntity<ResponseTemplate<List<RankingResponseDto>>> get20Rankings() {
        try {
            List<RankingResponseDto> rankings = bungLevelService.get20Rankings();
            ResponseTemplate<List<RankingResponseDto>> response =
                    new ResponseTemplate<>(SuccessCode.SUCCESS_200, rankings);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseTemplate<List<RankingResponseDto>> failResponse =
                    new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null);
            return ResponseEntity.status(FailureCode.SERVER_ERROR_500.getCode()).body(failResponse);
        }
    }




}
