package swyp_8th.bungmakase_backend.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.dto.bung_level.BungLogRequestDto;
import swyp_8th.bungmakase_backend.dto.bung_level.SuggestBungRequest;
import swyp_8th.bungmakase_backend.dto.profile.RankingResponseDto;
import swyp_8th.bungmakase_backend.dto.bung_level.UserLevelResponseDto;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.BungLevelService;
import swyp_8th.bungmakase_backend.service.BungLogService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/level")
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
public class BungLevelController {

    private final BungLogService bungLogService;
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

    @GetMapping("/top3")
    public ResponseEntity<ResponseTemplate<List<RankingResponseDto>>> getTop3() {
        try {
            List<RankingResponseDto> rankings = bungLevelService.getTop3();
            ResponseTemplate<List<RankingResponseDto>> response =
                    new ResponseTemplate<>(SuccessCode.SUCCESS_200, rankings);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseTemplate<List<RankingResponseDto>> failResponse =
                    new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null);
            return ResponseEntity.status(FailureCode.SERVER_ERROR_500.getCode()).body(failResponse);
        }
    }

    @PostMapping(value = "/daily",  consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseTemplate<Void>> addDailyBungLog(
            @CookieValue(value = "token") String token,
            @RequestPart("bungLogData") BungLogRequestDto bungLogData,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {

        try {
            UUID userId = jwtConfig.getUserIdFromToken(token);
            bungLogService.addDailyBungLog(userId, bungLogData, images);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseTemplate<>(SuccessCode.CREATED_201, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @PostMapping("/suggest")
    public ResponseEntity<ResponseTemplate<Void>> suggestBung(
            @RequestBody @Valid SuggestBungRequest request) {

        try {
            bungLogService.suggestBung(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseTemplate<>(SuccessCode.CREATED_201, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }




}
