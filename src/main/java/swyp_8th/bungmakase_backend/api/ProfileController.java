package swyp_8th.bungmakase_backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.dto.bung_level.BungLogRequestDto;
import swyp_8th.bungmakase_backend.dto.profile.*;
import swyp_8th.bungmakase_backend.exception.ResourceNotFoundException;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.ProfileService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final JwtConfig jwtConfig;

    @GetMapping("/user")
    public ResponseEntity<ResponseTemplate<UserProfileResponseDto>> getUserProfile(
            @CookieValue(value="token", required = false) String token
    ) {
        if(token == null || token.isEmpty()) {
            ResponseTemplate<UserProfileResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        }

        try {
            UserProfileResponseDto profile = profileService.getProfile(token);
            ResponseTemplate<UserProfileResponseDto> response =
                    new ResponseTemplate<>(SuccessCode.SUCCESS_200, profile);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            ResponseTemplate<UserProfileResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            failResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        }catch (Exception ex) {
            ResponseTemplate<UserProfileResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null);
            return ResponseEntity.status(FailureCode.SERVER_ERROR_500.getCode()).body(failResponse);
        }
    }

    @PutMapping(value = "/nickname", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseTemplate<UpdateNicknameResponseDto>> updateNickname(
            @CookieValue(value = "token", required = false) String token,
            @RequestPart("userData") UpdateNicknameRequestDto updateProfileDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        if (token == null || token.isEmpty()) {
            ResponseTemplate<UpdateNicknameResponseDto> failResponse = new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        }
        try {
            profileService.updateUserProfile(token, updateProfileDto, image);
            ResponseTemplate<UpdateNicknameResponseDto> response = new ResponseTemplate<>(SuccessCode.SUCCESS_200, null);
            response.setMessage("정보 변경 성공");
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            ResponseTemplate<UpdateNicknameResponseDto> failResponse = new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            failResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        } catch (IllegalArgumentException ex) {
            ResponseTemplate<UpdateNicknameResponseDto> failResponse = new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null);
            failResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(FailureCode.BAD_REQUEST_400.getCode()).body(failResponse);
        } catch (Exception ex) {
            ResponseTemplate<UpdateNicknameResponseDto> failResponse = new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null);
            return ResponseEntity.status(FailureCode.SERVER_ERROR_500.getCode()).body(failResponse);
        }
    }

    @GetMapping("/logs/list")
    public ResponseEntity<ResponseTemplate<List<LogListResponseDto>>> getUserBungLogs(
            @CookieValue(value = "token", required = false) String token) {

        if (token == null || token.isEmpty()) {
            ResponseTemplate<List<LogListResponseDto>> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        }
        try {
            List<LogListResponseDto> logs = profileService.getUserBungLogs(token);
            ResponseTemplate<List<LogListResponseDto>> response =
                    new ResponseTemplate<>(SuccessCode.SUCCESS_200, logs);
            response.setMessage("붕어빵 기록 리스트 조회 성공");
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            ResponseTemplate<List<LogListResponseDto>> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            failResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        } catch (Exception ex) {
            ResponseTemplate<List<LogListResponseDto>> failResponse =
                    new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null);
            return ResponseEntity.status(FailureCode.SERVER_ERROR_500.getCode()).body(failResponse);
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<ResponseTemplate<LogResponseDto>> getBungLogDetail(
            @CookieValue(value = "token", required = false) String token,
            @RequestParam("logId") String logId) {

        if (token == null || token.isEmpty()) {
            ResponseTemplate<LogResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        }

        try {
            LogResponseDto detail = profileService.getBungLogDetail(token, logId);
            ResponseTemplate<LogResponseDto> response =
                    new ResponseTemplate<>(SuccessCode.SUCCESS_200, detail);
            response.setMessage("붕어빵 기록 조회 성공");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            ResponseTemplate<LogResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.NOT_FOUND_404, null);
            failResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(FailureCode.NOT_FOUND_404.getCode()).body(failResponse);
        } catch (UnauthorizedException ex) {
            ResponseTemplate<LogResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            failResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        } catch (Exception ex) {
            ResponseTemplate<LogResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null);
            return ResponseEntity.status(FailureCode.SERVER_ERROR_500.getCode()).body(failResponse);
        }
    }


    /*@PutMapping(value = "/logs/edit",  consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseTemplate<Void>> addDailyBungLog(
            @RequestParam UUID logId,
            @CookieValue(value = "token") String token,
            @RequestPart("bungLogData") BungLogRequestDto bungLogData,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {

        try {
            UUID userId = jwtConfig.getUserIdFromToken(token);
            profileService.updateBungLog(userId, logId, bungLogData, images);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseTemplate<>(SuccessCode.CREATED_201, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }


    }*/
}
