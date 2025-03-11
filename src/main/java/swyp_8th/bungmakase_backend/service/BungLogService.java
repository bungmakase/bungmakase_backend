package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.domain.*;
import swyp_8th.bungmakase_backend.dto.bung_level.BungLogRequestDto;
import swyp_8th.bungmakase_backend.dto.bung_level.SuggestBungRequest;
import swyp_8th.bungmakase_backend.repository.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BungLogService {

    private final UserBungLogRepository userBungLogRepository;
    private final UserBungDogamRepository userBungDogamRepository;
    private final BungDogamRepository bungDogamRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserBungImageRepository userBungImageRepository;

    @Transactional
    public void addDailyBungLog(UUID userId, BungLogRequestDto bungLogData, List<MultipartFile> images) {
        log.info("===== addDailyBungLog START =====");
        log.info("User ID: {}", userId);
        log.info("Bung Log Data: {}", bungLogData);
        log.info("Image Count: {}", images != null ? images.size() : 0);

        try {
            // 1. 유저 검증
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            log.info("유저 검증 성공: {}", user.getNickname());

            user.setBungCount(user.getBungCount() + bungLogData.getBungCount());
            user.setRecentCount(user.getRecentCount() + bungLogData.getBungCount());
            user.setLevel((calculateUserLevel(user.getBungCount())));

            // 2. 붕 도감에서 붕어빵 찾기
            BungDogam bung = bungDogamRepository.findByBungName(bungLogData.getBungName())
                    .orElseThrow(() -> new RuntimeException("해당 붕어빵을 찾을 수 없습니다."));
            log.info("붕 도감 조회 성공: {}", bung.getBungName());

            // 3. 붕어빵 개수 검증
            if (bungLogData.getBungCount() < 1) {
                log.error("붕어빵 개수 오류: {}", bungLogData.getBungCount());
                throw new IllegalArgumentException("붕어빵 개수는 1개 이상이어야 합니다.");
            }

            // 4. 유저 붕 로그 생성
            UserBungLog bungLog = new UserBungLog();
            bungLog.setUser(user);
            bungLog.setBung(bung);
            bungLog.setCount(bungLogData.getBungCount());
            bungLog.setTags(String.join(",", bungLogData.getTags()));

            userBungLogRepository.save(bungLog);
            log.info("유저 붕 로그 저장 성공");

            // 5. 이미지 저장 (최대 5개)
            if (images != null && images.size() <= 5) {
                for (MultipartFile image : images) {
                    try {
                        // 이미지 업로드 (NCP Object Storage)
                        String imageUrl = null;
                        if (image != null && !image.isEmpty()) {
                            imageUrl = fileStorageService.uploadFile(image);
                            log.info("이미지 업로드 성공: {}", imageUrl);
                        }

                        UserBungImage bungImage = new UserBungImage();
                        bungImage.setUserBungLog(bungLog);
                        bungImage.setImageUrl(imageUrl);
                        userBungImageRepository.save(bungImage);
                        log.info("유저 붕 이미지 저장 성공");
                    } catch (Exception e) {
                        log.error("이미지 업로드 실패", e);
                        throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.");
                    }
                }
            } else if (images != null && images.size() > 5) {
                log.warn("이미지 개수 초과: {}", images.size());
                throw new IllegalArgumentException("이미지는 최대 5개까지 업로드할 수 있습니다.");
            }

            // 6. 유저 붕 도감 업데이트 (발견하지 않은 경우 추가)
            userBungDogamRepository.findByUserIdAndBungId(user.getId(), bung.getId())
                    .orElseGet(() -> {
                        UserBungDogam newBungDogam = new UserBungDogam();
                        newBungDogam.setUser(user);
                        newBungDogam.setBung(bung);
                        newBungDogam.setFound(true);
                        newBungDogam.setTags(String.join(",", bungLogData.getTags()));
                        log.info("새로운 붕 도감 항목 추가: {}", bung.getBungName());
                        return userBungDogamRepository.save(newBungDogam);
                    });

            log.info("===== addDailyBungLog SUCCESS =====");
        } catch (Exception e) {
            log.error("addDailyBungLog 실패", e);
            throw new RuntimeException("일일 붕어빵 로그 추가 중 오류가 발생했습니다.");
        }
    }

    public long calculateUserLevel(long bungCount) {
        if (bungCount >= 350) return 10L;
        if (bungCount >= 250) return 9L;
        if (bungCount >= 170) return 8L;
        if (bungCount >= 120) return 7L;
        if (bungCount >= 80) return 6L;
        if (bungCount >= 50) return 5L;
        if (bungCount >= 30) return 4L;
        if (bungCount >= 14) return 3L;
        if (bungCount >= 7) return 2L;
        return 1L;
    }

    @Transactional
    public void suggestBung(SuggestBungRequest request) {
        // 입력값 검증
        if (request.getBungName() == null || request.getBungName().isBlank() ||
                request.getTags() == null || request.getTags().isEmpty()) {
            log.warn("입력값 검증 실패: {}", request);
            throw new IllegalArgumentException("붕어빵 이름과 태그는 필수 입력값입니다.");
        }

        // 중복 체크
        bungDogamRepository.findByBungName(request.getBungName())
                .ifPresent(b -> {
                    throw new IllegalArgumentException("이미 존재하는 붕어빵입니다.");
                });

        // 붕어빵 생성
        BungDogam newBung = new BungDogam();
        newBung.setBungName(request.getBungName());


        bungDogamRepository.save(newBung);
        log.info("붕어빵 등록 성공: {}", newBung.getBungName());
    }
}
