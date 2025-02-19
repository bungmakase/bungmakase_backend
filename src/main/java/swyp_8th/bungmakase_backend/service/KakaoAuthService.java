package swyp_8th.bungmakase_backend.service;

import com.google.gson.Gson;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.domain.enums.UserAuthTypeEnum;
import swyp_8th.bungmakase_backend.dto.kakao_auth.KakaoUserInfoDto;
import swyp_8th.bungmakase_backend.dto.kakao_auth.OAuthToken;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private static final Logger log = LoggerFactory.getLogger(KakaoAuthService.class);

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USERINFO_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${kakao.client-id}")
    private String CLIENT_ID;

    /**
     * 카카오 인가 코드로 액세스 토큰 요청
     */
    public OAuthToken getOAuthToken(String code){

        RestTemplate rt = new RestTemplate();
        Gson gson = new Gson();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try {
            // POST 요청 실행
            ResponseEntity<String> response = rt.exchange(
                    KAKAO_TOKEN_URL, HttpMethod.POST, kakaoTokenRequest, String.class);

            // 응답 로그 확인
            log.info("카카오 API 응답: {}", response.getBody());

            // JSON 확인을 위해 직접 파싱

            // JSON 파싱 후 반환
            OAuthToken oAuthToken = gson.fromJson(response.getBody(), OAuthToken.class);

            log.info("발급된 액세스 토큰: {}", oAuthToken.getAccessToken());

            if (oAuthToken == null || oAuthToken.getAccessToken() == null) {
                throw new IllegalStateException("OAuthToken이 null이거나 accessToken이 없음!");
            }

            return oAuthToken;
        } catch (Exception e) {
            log.error("카카오 OAuth 토큰 요청 실패", e);
            throw new RuntimeException("카카오 OAuth 토큰 요청 실패: " + e.getMessage());
        }
    }



    /**
     * 액세스 토큰으로 유저 정보 요청
     */
    public KakaoUserInfoDto getUserInfo(String accessToken) {
        log.info("카카오 액세스 토큰: {}", accessToken);

        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("accessToken이 없습니다");
        }

        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //http 헤더(headers)를 가진 엔티티
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        //reqUrl로 Http 요청 , POST 방식
        ResponseEntity<String> response =
                rt.exchange(KAKAO_USERINFO_URL, HttpMethod.GET, kakaoProfileRequest, String.class);

        KakaoUserInfoDto userInfo = new KakaoUserInfoDto(response.getBody());

        return userInfo;
    }


    /**
     * 유저 정보 저장 및 JWT 발급
     */
    public String processUserLogin(KakaoUserInfoDto userInfo) {

        // 기존 유저 조회
        Optional<Users> existingUser = userRepository.findByOauthId(userInfo.getId());
        Users user;

        if (existingUser.isPresent()) {
            // 기존 유저가 존재하면 정보 업데이트
            user = existingUser.get();
            user.setNickname(userInfo.getNickname());
            user.setImage_url(userInfo.getImage_url());
        } else {
            // 신규 유저 생성
            user = new Users();
            user.setOauthId(userInfo.getId()); // OAuth ID 설정
            user.setNickname(userInfo.getNickname());
            user.setImage_url(userInfo.getImage_url());
            user.setLevel(1L);
            user.setAuthType(UserAuthTypeEnum.KAKAO);
            userRepository.save(user); // DB 저장
        }

        return jwtConfig.generateToken(user.getId());

    }

}
