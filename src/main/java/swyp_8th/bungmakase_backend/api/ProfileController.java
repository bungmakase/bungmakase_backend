package swyp_8th.bungmakase_backend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.api.dto.LogListResponseDto;
import swyp_8th.bungmakase_backend.api.dto.UpdateNicknameRequestDto;
import swyp_8th.bungmakase_backend.api.dto.UpdateNicknameResponseDto;
import swyp_8th.bungmakase_backend.api.dto.UserProfileResponseDto;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.ProfileService;

import java.util.List;

@Controller
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

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

    @GetMapping("/logs")
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



}
