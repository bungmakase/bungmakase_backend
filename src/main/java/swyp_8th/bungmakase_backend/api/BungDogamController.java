package swyp_8th.bungmakase_backend.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.domain.UserBungDogam;
import swyp_8th.bungmakase_backend.dto.bung_dogam.BungListResponseDto;
import swyp_8th.bungmakase_backend.dto.bung_dogam.BungResponseDto;
import swyp_8th.bungmakase_backend.exception.InvalidTokenException;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.repository.UserBungDogamRepository;
import swyp_8th.bungmakase_backend.service.BungDogamService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
@RequestMapping("/api/dogam")
public class BungDogamController {

    private final BungDogamService bungDogamService;
    private final JwtConfig jwtConfig;

    @GetMapping("/list")
    public ResponseEntity<ResponseTemplate<List<BungListResponseDto>>> getAllBungDogam() {
        try {
            List<BungDogam> bungDogamList = bungDogamService.getAllBungDogam();

            // BungListResponseDto로 변환
            List<BungListResponseDto> bungList = bungDogamList.stream()
                    .map(bung -> new BungListResponseDto(
                            bung.getId().toString(),
                            bung.getBungName()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, bungList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @GetMapping("/userList")
    public ResponseEntity<ResponseTemplate<List<BungListResponseDto>>> getUserBungDogam(
            @CookieValue(value = "token") String token) {

        try {
            // 토큰에서 유저 ID 추출
            UUID userId = jwtConfig.getUserIdFromToken(token);

            // 유저가 발견한 붕어빵 리스트 조회
            List<UserBungDogam> userBungList = bungDogamService.getUserFoundBung(userId);

            // BungListResponseDto로 변환
            List<BungListResponseDto> responseList = userBungList.stream()
                    .map(ubd -> new BungListResponseDto(
                            ubd.getBung().getId().toString(),
                            ubd.getBung().getBungName()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, responseList));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseTemplate<BungResponseDto>> getBungDogam(
            @CookieValue(value = "token") String token,
            @RequestParam("bungId") int bungId) {

        try {
            // 1. 토큰에서 유저 ID 추출
            UUID userId = jwtConfig.getUserIdFromToken(token);

            // 2. 특정 bungId에 대한 도감 데이터 조회
            UserBungDogam userBungDogam = bungDogamService.getUserBungDogam(userId, bungId);

            // 3. BungResponseDto로 변환
            BungResponseDto responseDto = new BungResponseDto(
                    userBungDogam.getBung().getId().toString(),
                    userBungDogam.getBung().getBungName(),
                    Arrays.asList(userBungDogam.getTags().split(","))
            );

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, responseDto));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @Operation(
            summary = "유저 붕어빵 도감 추가용 임시 API",
            description = "특정 유저의 붕어빵 도감에 새로운 붕어빵을 추가 (bungId는 1~11)"
    )
    @PostMapping("/save")
    public ResponseEntity<String> saveUserBungDogam(@RequestParam String nickname, @RequestParam int bungId, @RequestParam String tags) {
        try {
            UserBungDogam savedBungDogam = bungDogamService.saveUserBungDogam(nickname, bungId, tags);
            return ResponseEntity.ok("UserBungDogam 저장 성공! ID: " + savedBungDogam.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("에러: " + e.getMessage());
        }
    }
}
