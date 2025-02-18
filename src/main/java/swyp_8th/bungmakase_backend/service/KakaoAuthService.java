package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.dto.KakaoUserInfoDto;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final WebClient webClient; // WebClient 주입

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USERINFO_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${kakao.client-id}")
    private String CLIENT_ID;

    /**
     * 카카오 인가 코드로 액세스 토큰 요청
     */
    public String getAccessToken(String code) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(KAKAO_TOKEN_URL)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block() // 동기 방식으로 반환
                .getAccessToken();
    }

    /**
     * 카카오 토큰 응답 DTO
     */

    private static class TokenResponse {
        @Value("${access_token}")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }
    }


    /**
     * 액세스 토큰으로 유저 정보 요청
     */
    public KakaoUserInfoDto getUserInfo (String accessToken) {
        return webClient.get()
                .uri(KAKAO_USERINFO_URL)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoUserInfoDto.class)
                .block(); // 동기 방식으로 반환
    }

    /**
     * 유저 정보 저장 및 JWT 발급
     */
    public String processUserLogin(KakaoUserInfoDto userInfo) {
        // 카카오 ID를 String으로 변환
        String oauthId = String.valueOf(userInfo.getId());

        // 기존 유저 조회
        Optional<Users> existingUser = userRepository.findByKakaoId(userInfo.getId());
        Users user;

        if (existingUser.isPresent()) {
            // 기존 유저가 존재하면 정보 업데이트
            user = existingUser.get();
            user.setNickname(userInfo.getKakaoAccount().getProfile().getNickname());
            user.setImage_url(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
        } else {
            // 신규 유저 생성
            user = new Users();
            user.setOauthId(oauthId); // OAuth ID 설정
            user.setNickname(userInfo.getKakaoAccount().getProfile().getNickname());
            user.setImage_url(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
            userRepository.save(user); // DB 저장
        }

        return jwtConfig.generateToken(user.getId());

    }

}
