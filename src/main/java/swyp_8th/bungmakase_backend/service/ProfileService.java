package swyp_8th.bungmakase_backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.api.dto.LogListResponseDto;
import swyp_8th.bungmakase_backend.api.dto.UpdateNicknameRequestDto;
import swyp_8th.bungmakase_backend.api.dto.UserProfileResponseDto;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.UserBungImage;
import swyp_8th.bungmakase_backend.domain.UserBungLog;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.repository.MyUserRepository;
import swyp_8th.bungmakase_backend.repository.UserBungLogRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final MyUserRepository myUserRepository;
    private final JwtConfig jwtConfig;
    private final FileStorageService fileStorageService;
    private final UserBungLogRepository userBungLogRepository;


    public ProfileService(MyUserRepository myUserRepository, JwtConfig jwtConfig, FileStorageService fileStorageService, UserBungLogRepository userBungLogRepository) {
        this.myUserRepository = myUserRepository;
        this.jwtConfig = jwtConfig;
        this.fileStorageService = fileStorageService;
        this.userBungLogRepository = userBungLogRepository;
    }

    public UserProfileResponseDto getProfile(String token) {
        //Using JwtConfig verify the token and extract the user Id(UUID)
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = myUserRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("user not found"));


        return new UserProfileResponseDto(user.getNickname(), user.getLevel(), user.getImage_url());

    }

    @Transactional
    public void updateUserProfile(String token, UpdateNicknameRequestDto updateProfileDto, MultipartFile image) {
        // 토큰 검증 및 사용자 ID 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = myUserRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // 닉네임 업데이트
        user.setNickname(updateProfileDto.getNickname());

        // 이미지 파일이 제공되면 업로드 후 이미지 URL 업데이트
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(image);
            user.setImage_url(imageUrl);
        }

        // 변경 사항 저장 (트랜잭션 내에서 자동 플러시)
        myUserRepository.save(user);
    }

    @Transactional
    public List<LogListResponseDto> getUserBungLogs(String token) {
        // 토큰을 통해 사용자 ID(UUID) 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = myUserRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // 사용자의 붕어빵 기록들을 최신 순으로 조회
        List<UserBungLog> logs = userBungLogRepository.findByUserOrderByLogDateDesc(user);

        // 각 기록에 대해, 첫 번째 이미지 URL(있으면)을 DTO로 매핑
        return logs.stream().map(log -> {
            String imageUrl = null;
            List<UserBungImage> images = log.getUserBungImages();
            if (images != null && !images.isEmpty()) {
                // 첫 번째 이미지의 URL을 사용 (필요에 따라 여러 이미지 처리 가능)
                imageUrl = images.get(0).getImageUrl();
            }
            return new LogListResponseDto(log.getId().toString(), imageUrl);
        }).collect(Collectors.toList());
    }




}
