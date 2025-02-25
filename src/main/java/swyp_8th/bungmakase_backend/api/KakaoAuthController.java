package swyp_8th.bungmakase_backend.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp_8th.bungmakase_backend.dto.kakao_auth.KakaoUserInfoDto;
import swyp_8th.bungmakase_backend.globals.CookieUtil;
import swyp_8th.bungmakase_backend.service.KakaoAuthService;

import java.io.IOException;


@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final CookieUtil cookieUtill;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";

    @GetMapping("/kakao")
    public void getKakaoLoginUrl(@RequestParam(required =false) String state, HttpServletResponse response) throws Exception{

        String loginUrl;

        if ("local".equals(state)) {
            loginUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&state=local",
                    KAKAO_AUTH_URL, clientId, redirectUri);
        }

        else {
            loginUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code",
                    KAKAO_AUTH_URL, clientId, redirectUri);
        }

        response.sendRedirect(loginUrl);

    }

    @GetMapping("/kakao/callback")
    public void kakaoLogin(@RequestParam String code, @RequestParam(required = false) String state,
    HttpServletResponse response) throws IOException{
        // 1. 카카오 인가코드로 엑세스 토큰 발급
        String accessToken = kakaoAuthService.getOAuthToken(code).getAccessToken();

        // 2. 엑세스 토큰으로 유저 정보 조회 및 저장
        KakaoUserInfoDto userInfo = kakaoAuthService.getUserInfo(accessToken);
        String jwtToken = kakaoAuthService.processUserLogin(userInfo);

    
        String frontendUrl;
        String cookieDomain;

        if ("local".equals(state)) {
            frontendUrl = "https://localhost:3001";
            cookieDomain = "localhost"; // 로컬용 도메인
        } else {
            frontendUrl = "https://bungmakase.vercel.app";
            cookieDomain = ".vercel.app"; // 운영 도메인
        }

        response.setHeader("Set-Cookie", cookieUtill.createCookie(jwtToken, cookieDomain).toString());
        response.sendRedirect(frontendUrl);
    }



}
