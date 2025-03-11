package swyp_8th.bungmakase_backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.domain.UserBungImage;
import swyp_8th.bungmakase_backend.domain.UserBungLog;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.dto.bung_level.BungLogRequestDto;
import swyp_8th.bungmakase_backend.dto.profile.*;
import swyp_8th.bungmakase_backend.exception.ResourceNotFoundException;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.repository.BungDogamRepository;
import swyp_8th.bungmakase_backend.repository.UserBungImageRepository;
import swyp_8th.bungmakase_backend.repository.UserBungLogRepository;
import swyp_8th.bungmakase_backend.repository.UserRepository;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final FileStorageService fileStorageService;
    private final UserBungLogRepository userBungLogRepository;
    private final BungDogamRepository bungDogamRepository;
    private final UserBungImageRepository userBungImageRepository;


    // 모든 유저의 recentCount 일주일마다 초기화
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void resetRecentCounts() {

        userRepository.resetRecentCounts();

    }



    public UserProfileResponseDto getProfile(String token) {
        //Using JwtConfig verify the token and extract the user Id(UUID)
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("user not found"));


        return new UserProfileResponseDto(user.getNickname(), user.getLevel(), user.getImage_url());

    }

    @Transactional
    public void updateUserProfile(String token, UpdateNicknameRequestDto updateProfileDto, MultipartFile image) {
        // 토큰 검증 및 사용자 ID 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // 닉네임 업데이트
        user.setNickname(updateProfileDto.getNickname());

        // 이미지 파일이 제공되면 업로드 후 이미지 URL 업데이트
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(image);
            user.setImage_url(imageUrl);
        }

        // 변경 사항 저장 (트랜잭션 내에서 자동 플러시)
        userRepository.save(user);
    }

    @Transactional
    public List<LogListResponseDto> getUserBungLogs(String token) {
        // 토큰을 통해 사용자 ID(UUID) 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = userRepository.findById(userId)
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

    @Transactional
    public LogResponseDto getBungLogDetail(String token, String logId) {
        // JWT 토큰에서 사용자 ID(UUID) 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // logId 문자열을 UUID로 변환
        UUID logUUID;
        try {
            logUUID = UUID.fromString(logId);
        } catch (IllegalArgumentException ex) {
            throw new ResourceNotFoundException("붕어빵 기록을 찾을 수 없습니다.");
        }

        // UserBungLog 조회 (findById는 Optional 제공)
        UserBungLog bungLog = userBungLogRepository.findById(logUUID)
                .orElseThrow(() -> new ResourceNotFoundException("붕어빵 기록을 찾을 수 없습니다."));

        // 조회한 기록이 요청한 사용자 소유인지 확인
        if (!bungLog.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("붕어빵 기록을 찾을 수 없습니다.");
        }

        // 이미지 URL 리스트 추출 (여러 이미지가 첨부되어 있을 경우)
        List<String> imageUrls = bungLog.getUserBungImages().stream()
                .map(UserBungImage::getImageUrl)
                .collect(Collectors.toList());

        // 태그 문자열을 콤마 구분해서 List<String>으로 변환 (저장 시 "달콤,바삭")
        List<String> tags = null;
        if (bungLog.getTags() != null && !bungLog.getTags().isEmpty()) {
            tags = List.of(bungLog.getTags().split(","));
        }

        // BungDogam의 bungName 조회
        String bungName = bungLog.getBung().getBungName();

        return new LogResponseDto(
                bungLog.getId().toString(),
                bungName,
                imageUrls,
                bungLog.getLogDate(),
                bungLog.getCount(),
                tags
        );
    }

    @Transactional
    public void updateBungLog(UUID userId, UUID logId, BungLogRequestDto updateDto, List<MultipartFile> images) {
        log.info("붕어빵 로그 수정 시작: logId={}, userId={}", logId, userId);

        // 붕어빵 로그 찾기
        UserBungLog bungLog = userBungLogRepository.findByIdAndUserId(logId, userId)
                .orElseThrow(() -> new RuntimeException("해당 붕어빵 로그를 찾을 수 없습니다."));

        // User 붕 개수 다시 설정 (recent count는 변경 X)
        Users user = userRepository.findUsersById(userId);
        user.setBungCount(user.getBungCount()-bungLog.getCount()+updateDto.getBungCount());

        // 붕어빵 도감에서 붕어빵 이름 찾기
        BungDogam newBung = bungDogamRepository.findByBungName(updateDto.getBungName())
                .orElseThrow(() -> new RuntimeException("해당 붕어빵을 찾을 수 없습니다."));

        // 붕어빵 로그 정보 수정
        bungLog.setBung(newBung);
        bungLog.setCount(updateDto.getBungCount());
        bungLog.setTags(String.join(",", updateDto.getTags()));

        userBungLogRepository.save(bungLog);



        // 기존 이미지 삭제
        List<UserBungImage> existingImages = bungLog.getUserBungImages();
        for (UserBungImage image : existingImages) {
            fileStorageService.deleteFile(image.getImageUrl());  // 파일 스토리지에서 삭제
            userBungImageRepository.delete(image);  // DB에서 삭제
        }

        // 새로운 이미지 업로드
        if (images != null && images.size() <= 5) {
            for (MultipartFile image : images) {

                String imageUrl = fileStorageService.uploadFile(image);
                UserBungImage userBungImage = new UserBungImage();
                userBungImage.setImageUrl(imageUrl);
                userBungImage.setUserBungLog(bungLog);
                userBungImage.setUploadedAt(bungLog.getLogDate());
                userBungImageRepository.save(userBungImage);
                }
            }
        }



    }

