package swyp_8th.bungmakase_backend.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp_8th.bungmakase_backend.dto.KakaoUserInfoDto;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.KakaoAuthService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000"})
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

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

    @GetMapping("/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException{
        // 1. 카카오 인가코드로 엑세스 토큰 발급
        String accessToken = kakaoAuthService.getOAuthToken(code).getAccessToken();

        // 2. 엑세스 토큰으로 유저 정보 조회 및 저장
        KakaoUserInfoDto userInfo = kakaoAuthService.getUserInfo(accessToken);
        String jwtToken = kakaoAuthService.processUserLogin(userInfo);

        // 3. 붕 도감 페이지로 리다이렉트 (JWT 포함)
        response.sendRedirect("/api/dogam/list");
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
    }



}
