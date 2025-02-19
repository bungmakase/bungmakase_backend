package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.domain.enums.UserAuthTypeEnum;
import swyp_8th.bungmakase_backend.dto.auth.SignupRequestDto;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService; // 파일 저장 서비스
    private final JwtConfig jwtConfig;

    public ResponseTemplate checkEmailAvailability(String email) {

        if (userRepository.existsByEmail(email)) {
            return new ResponseTemplate(FailureCode.USED_EMAIL_409, null);
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
                .authType(UserAuthTypeEnum.EMAIL)
                .build();

        userRepository.save(newUser);

        return jwtConfig.generateToken(newUser.getId());
    }
}