package swyp_8th.bungmakase_backend.api;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.dto.auth.EmailLoginRequestDto;
import swyp_8th.bungmakase_backend.dto.auth.SignupRequestDto;
import swyp_8th.bungmakase_backend.globals.CookieUtil;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtill;

    @GetMapping("/check-email")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> checkEmail(@RequestParam String email) {
        ;
        try {
            ResponseTemplate response = authService.checkEmailAvailability(email);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        }

    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> checkNickname(@RequestParam String nickname) {;
        try{
            ResponseTemplate response = authService.checkNicknameAvailability(nickname);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        }
    }

    @PostMapping(value = "/signup/email", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseTemplate<Map<String, String>>> signup(
            @RequestPart("userData") SignupRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile image, HttpServletResponse response) {

        try {
            // 회원가입 처리 및 JWT 생성
            String jwt = authService.signup(requestDto, image);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, cookieUtill.createCookie(jwt, ".vercel.app").toString());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .headers(headers)
                    .body(new ResponseTemplate<>(SuccessCode.CREATED_201, null));


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        }


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

    @PostMapping(value = "/login/email")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> loginWithEmail(
            @RequestBody EmailLoginRequestDto request) {

        try {
            String jwt = authService.loginWithEmail(request);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, cookieUtill.createCookie(jwt, ".vercel.app").toString());

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, null));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> logout(@CookieValue(value = "token") String token){
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, null));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

}
