package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.GuestSession;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.domain.enums.UserAuthTypeEnum;
import swyp_8th.bungmakase_backend.dto.auth.EmailLoginRequestDto;
import swyp_8th.bungmakase_backend.dto.auth.SignupRequestDto;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.repository.GuestSessionRepository;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final GuestSessionRepository guestSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService; // 파일 저장 서비스
    private final JwtConfig jwtConfig;

    public ResponseTemplate checkEmailAvailability(String email) {

        if (userRepository.existsByEmail(email)) {
            return new ResponseTemplate(FailureCode.USED_EMAIL_409, null);
        }

        return new ResponseTemplate(SuccessCode.SUCCESS_200, null);
    }

    public ResponseTemplate checkNicknameAvailability(String nickname) {

        if (userRepository.existsByNickname(nickname)) {
            return new ResponseTemplate(FailureCode.USED_NICKNAME_409, null);
        }

        return new ResponseTemplate(SuccessCode.SUCCESS_200, null);
    }



    @Transactional
    public String signup(SignupRequestDto requestDto, MultipartFile profileImage) {


        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 이미지 업로드 (NCP Object Storage)
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = fileStorageService.uploadFile(profileImage);
        }

        Users newUser = Users.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .image_url(profileImageUrl)
                .level(1L)
                .bungCount(0L)
                .recentCount(0L)
                .authType(UserAuthTypeEnum.EMAIL)
                .build();

        userRepository.save(newUser);

        return jwtConfig.generateToken(newUser.getId());
    }

    public Users createGuestUser() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(24);  // 24시간 유지

        Users guestUser = Users.builder()
                .createdAt(now)
                .authType(UserAuthTypeEnum.GUEST)
                .build();

        userRepository.save(guestUser);

        GuestSession guestSession = GuestSession.builder()
                .user(guestUser)
                .expiresAt(now.plusHours(24))
                .build();

        guestSessionRepository.save(guestSession);

        return guestUser;

    }

    // 매일 자정에 만료된 게스트 세션 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredGuestSessions() {
        LocalDateTime now = LocalDateTime.now();
        guestSessionRepository.deleteExpiredSessions(now);
    }

    public String loginWithEmail(EmailLoginRequestDto request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패 - 등록되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("로그인 실패 - 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        return jwtConfig.generateToken(user.getId());
    }
}