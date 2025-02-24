package swyp_8th.bungmakase_backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.api.dto.*;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.domain.UserBungImage;
import swyp_8th.bungmakase_backend.domain.UserBungLog;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.exception.ResourceNotFoundException;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.repository.BungDogamRepository;
import swyp_8th.bungmakase_backend.repository.MyUserRepository;
import swyp_8th.bungmakase_backend.repository.UserBungImageRepository;
import swyp_8th.bungmakase_backend.repository.UserBungLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final MyUserRepository myUserRepository;
    private final JwtConfig jwtConfig;
    private final FileStorageService fileStorageService;
    private final UserBungLogRepository userBungLogRepository;
    private final BungDogamRepository bungDogamRepository;
    private final UserBungImageRepository userBungImageRepository;



    public ProfileService(MyUserRepository myUserRepository,
                          JwtConfig jwtConfig,
                          FileStorageService fileStorageService,
                          UserBungLogRepository userBungLogRepository,
                          BungDogamRepository bungDogamRepository,
                          UserBungImageRepository userBungImageRepository) {
        this.myUserRepository = myUserRepository;
        this.jwtConfig = jwtConfig;
        this.fileStorageService = fileStorageService;
        this.userBungLogRepository = userBungLogRepository;
        this.bungDogamRepository = bungDogamRepository;
        this.userBungImageRepository = userBungImageRepository;

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

    @Transactional
    public LogResponseDto getBungLogDetail(String token, String logId) {
        // JWT 토큰에서 사용자 ID(UUID) 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = myUserRepository.findById(userId)
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

        // logDate의 날짜 부분 추출
        LocalDate date = bungLog.getLogDate().toLocalDate();

        // BungDogam의 bungName 조회
        String bungName = bungLog.getBung().getBungName();

        return new LogResponseDto(
                bungLog.getId().toString(),
                bungName,
                imageUrls,
                date,
                bungLog.getCount(),
                tags
        );
    }

    @Transactional
    public UpdateLogResponseDto updateBungLog(String token, String logId, UpdateLogRequestDto updateDto, MultipartFile image) {
        // 1. JWT 토큰에서 사용자 ID 추출
        UUID userId = jwtConfig.getUserIdFromToken(token);
        Users user = myUserRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // 2. logId를 UUID로 파싱
        UUID logUUID;
        try {
            logUUID = UUID.fromString(logId);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("붕어빵 기록을 찾을 수 없습니다.");
        }

        // 3. UserBungLog 기록 조회
        UserBungLog bungLog = userBungLogRepository.findById(logUUID)
                .orElseThrow(() -> new ResourceNotFoundException("붕어빵 기록을 찾을 수 없습니다."));
        // 소유자 확인
        if (!bungLog.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("붕어빵 기록을 찾을 수 없습니다.");
        }

        // 4. 수정 데이터 적용
        bungLog.setCount((long) updateDto.getBungCount());
        bungLog.setTags(String.join(",", updateDto.getTags()));
        bungLog.setLogDate(updateDto.getDate().atStartOfDay());

        // 5. 붕어빵 이름 수정 처리: 만약 bungName이 달라지면, 해당 BungDogam 조회(없으면 생성) 후 연결 업데이트
        if (!bungLog.getBung().getBungName().equals(updateDto.getBungName())) {
            BungDogam newBung = bungDogamRepository.findByBungName(updateDto.getBungName())
                    .orElseGet(() -> {
                        BungDogam b = new BungDogam();
                        b.setBungName(updateDto.getBungName());
                        return bungDogamRepository.save(b);
                    });
            bungLog.setBung(newBung);
        }

        // 6. 기록 저장 (업데이트)
        bungLog = userBungLogRepository.save(bungLog);

        // 7. 이미지 파일 처리: 새 이미지가 첨부된 경우, 업로드 후 새로운 이미지 엔티티 생성
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(image);
            UserBungImage newImage = new UserBungImage();
            newImage.setUserBungLog(bungLog);
            newImage.setImageUrl(imageUrl);
            newImage.setUploadedAt(LocalDateTime.now());
            userBungImageRepository.save(newImage);
        }

        // 8. 응답 DTO 구성: 기존 기록에 속한 모든 이미지 URL 추출, 태그 문자열 분리
        List<String> imageUrls = bungLog.getUserBungImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());
        List<String> tagList = (bungLog.getTags() != null && !bungLog.getTags().isEmpty())
                ? List.of(bungLog.getTags().split(","))
                : null;

        return new UpdateLogResponseDto(
                bungLog.getId().toString(),
                bungLog.getBung().getBungName(),
                bungLog.getCount().intValue(),
                tagList,
                imageUrls,
                bungLog.getLogDate().toLocalDate()
        );
    }



}
