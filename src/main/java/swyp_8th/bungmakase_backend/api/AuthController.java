package swyp_8th.bungmakase_backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:5173"})
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";

    @GetMapping("/kakao")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> getKakaoLoginUrl() {
        try {
            String loginUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code",
                    KAKAO_AUTH_URL, clientId, redirectUri);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("loginUrl", loginUrl);

            return ResponseEntity.ok(new ResponseTemplate<>(SuccessCode.SUCCESS_200, responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }



}
