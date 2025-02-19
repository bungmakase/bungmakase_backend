package swyp_8th.bungmakase_backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.dto.auth.SignupRequestDto;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000"})
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/check-email")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> checkEmail(@RequestParam String email) {;
        ResponseTemplate response = authService.checkEmailAvailability(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> checkNickname(@RequestParam String nickname) {;
        ResponseTemplate response = authService.checkNicknameAvailability(nickname);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping(value = "/signup/email", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseTemplate<Map<String, String>>> signup(
            @RequestPart("userData") SignupRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String jwt = authService.signup(requestDto, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(new ResponseTemplate<>(SuccessCode.CREATED_201, null));
    }

    @PostMapping("/guest")
    public ResponseEntity<ResponseTemplate<Map<String, Object>>> startGuestMode() {
        Map<String, Object> response = new HashMap<>();
        try {
            Users guestUser = authService.createGuestUser();

            return ResponseEntity.ok(new ResponseTemplate<>(SuccessCode.CREATED_201,null));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }



}
