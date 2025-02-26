package swyp_8th.bungmakase_backend.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp_8th.bungmakase_backend.dto.kakao_auth.KakaoUserInfoDto;
import swyp_8th.bungmakase_backend.globals.CookieUtil;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.KakaoAuthService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final CookieUtil cookieUtill;

    @Value("${kakao.client-id}")
    private String clientId;

//    @Value("${kakao.redirect-uri}")
//    private String redirectUri;

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";

//    @GetMapping("/kakao")
//    public void getKakaoLoginUrl(@RequestParam(required =false) String state, HttpServletResponse response) throws Exception{
//
//        String loginUrl;
//
//        if ("local".equals(state)) {
//            loginUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&state=local",
//                    KAKAO_AUTH_URL, clientId, redirectUri);
//        }
//
//        else {
//            loginUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code",
//                    KAKAO_AUTH_URL, clientId, redirectUri);
//        }
//
//        response.sendRedirect(loginUrl);
//
//    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<ResponseTemplate<Map<String, String>>> kakaoLogin(@RequestParam String code, @RequestParam(required = false) String state) throws IOException{

        String accessToken = kakaoAuthService.getOAuthToken(code, state).getAccessToken();

        // 엑세스 토큰으로 유저 정보 조회 및 저장
        KakaoUserInfoDto userInfo = kakaoAuthService.getUserInfo(accessToken);
        String jwtToken = kakaoAuthService.processUserLogin(userInfo);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("token", jwtToken);


        // 3. 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseTemplate<>(SuccessCode.CREATED_201, responseData));
    }
}