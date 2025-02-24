package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.domain.BungShop;
import swyp_8th.bungmakase_backend.domain.BungShopImage;
import swyp_8th.bungmakase_backend.domain.BungShopReview;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.dto.bung_map.AddShopRequest;
import swyp_8th.bungmakase_backend.dto.bung_map.AddShopResponse;
import swyp_8th.bungmakase_backend.dto.bung_map.MarkerResponseDto;
import swyp_8th.bungmakase_backend.dto.bung_map.ShopReviewRequest;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.repository.BungShopImageRepository;
import swyp_8th.bungmakase_backend.repository.BungShopRepository;
import swyp_8th.bungmakase_backend.repository.BungShopReviewRepository;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BungMapService {

    private final BungShopRepository bungShopRepository;
    private final BungShopReviewRepository bungShopReviewRepository;
    private final BungShopImageRepository bungShopImageRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // 전체 마커 데이터 조회
    public List<MarkerResponseDto> getAllMarkers() {
        List<BungShop> shops = bungShopRepository.findAll();

        return shops.stream()
                .map(shop -> new MarkerResponseDto(
                        shop.getId().toString(),
                        shop.getShopName(),
                        shop.getShopAddress(),
                        shop.getLatitude(),
                        shop.getLongitude(),
                        shop.getStar(),
                        shop.getStartTime() != null ? shop.getStartTime().toString() : "09:00",
                        shop.getEndTime() != null ? shop.getEndTime().toString() : "18:00",
                        Arrays.asList(shop.getTastes().split(","))
                ))
                .collect(Collectors.toList());
    }

    // 가게 이름으로 검색 (최대 10개)
    public List<MarkerResponseDto> searchMarkersByShopName(String shopName) {
        Pageable limit = PageRequest.of(0, 10);  // 첫 페이지, 10개 제한
        List<BungShop> shops = bungShopRepository.searchByShopName(shopName, limit);

        return shops.stream()
                .map(shop -> new MarkerResponseDto(
                        shop.getId().toString(),
                        shop.getShopName(),
                        shop.getShopAddress(),
                        shop.getLatitude(),
                        shop.getLongitude(),
                        shop.getStar() != null ? shop.getStar() : 5,
                        shop.getStartTime() != null ? shop.getStartTime().toString() : "09:00",
                        shop.getEndTime() != null ? shop.getEndTime().toString() : "18:00",
                        Arrays.asList(shop.getTastes().split(","))
                ))
                .collect(Collectors.toList());
    }

    // shopId로 특정 가게 조회
    public Optional<MarkerResponseDto> getMarkerByShopId(UUID shopId) {
        return bungShopRepository.findById(shopId)
                .map(shop -> new MarkerResponseDto(
                        shop.getId().toString(),
                        shop.getShopName(),
                        shop.getShopAddress(),
                        shop.getLatitude(),
                        shop.getLongitude(),
                        shop.getStar() != null ? shop.getStar() : 5,
                        shop.getStartTime() != null ? shop.getStartTime().toString() : "09:00",
                        shop.getEndTime() != null ? shop.getEndTime().toString() : "18:00",
                        Arrays.asList(shop.getTastes().split(","))
                ));
    }

    @Transactional
    public void addShopReview(UUID userId, ShopReviewRequest reviewData, List<MultipartFile> images) {
        log.info("리뷰 작성 시작 - UserID: {}, ShopID: {}", userId, reviewData.getShopId());

        UUID shopId = UUID.fromString(reviewData.getShopId());

        // 1. 유저 검증
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));

        // 2. 붕어빵 가게 검증
        BungShop bungShop = bungShopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 가게입니다."));

        // 3. 리뷰 생성
        BungShopReview review = new BungShopReview();
        review.setUser(user);
        review.setBungShop(bungShop);
        review.setBungName(reviewData.getBungName());
        review.setReviewText(reviewData.getReviewText());
        review.setStar(reviewData.getStar());

        bungShopReviewRepository.save(review);
        log.info("리뷰 저장 성공 - 리뷰ID: {}", review.getId());

        // 4. 이미지 업로드 (최대 5개)
        if (images != null && images.size() <= 5) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = fileStorageService.uploadFile(image);
                    BungShopImage shopImage = new BungShopImage();
                    shopImage.setBungShop(bungShop);
                    shopImage.setBungShopReview(review);
                    shopImage.setImageUrl(imageUrl);

                    bungShopImageRepository.save(shopImage);
                    log.info("이미지 업로드 성공 - URL: {}", imageUrl);
                }
            }
        }
        log.info("리뷰 작성 완료");
    }

    @Transactional
    public AddShopResponse addNewShop(AddShopRequest shopData, List<MultipartFile> images) {
        // 가게 이름과 주소로 중복 확인
        bungShopRepository.findByShopNameAndShopAddress(shopData.getShopName(), shopData.getAddress())
                .ifPresent(shop -> {
                    throw new IllegalArgumentException("이미 등록된 가게입니다.");
                });

        // BungShop 객체 생성
        BungShop newShop = new BungShop();
        newShop.setShopName(shopData.getShopName());
        newShop.setLatitude(shopData.getLatitude());
        newShop.setLongitude(shopData.getLongitude());
        newShop.setShopAddress(shopData.getAddress());
        newShop.setPhoneNumber(shopData.getPhone());
        newShop.setStartTime(LocalTime.parse(shopData.getStartTime()));
        newShop.setEndTime(LocalTime.parse(shopData.getEndTime()));
        newShop.setTastes(String.join(",", shopData.getTastes())); // 리스트를 문자열로 변환

        BungShop savedShop = bungShopRepository.save(newShop);
        // 이미지 업로드
        if (images != null && images.size() <= 5) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = fileStorageService.uploadFile(image);
                    BungShopImage shopImage = new BungShopImage();
                    shopImage.setBungShop(savedShop);
                    shopImage.setImageUrl(imageUrl);

                    bungShopImageRepository.save(shopImage);
                    log.info("이미지 업로드 성공 - URL: {}", imageUrl);
                }
            }
        }

        // 가게 저장

        log.info("새로운 가게 추가 성공: {}", savedShop.getShopName());

        // 응답 반환
        String shopId = String.valueOf(savedShop.getId());
        AddShopResponse response = new AddShopResponse();
        response.setShopId(shopId);

        return response;
    }
}
