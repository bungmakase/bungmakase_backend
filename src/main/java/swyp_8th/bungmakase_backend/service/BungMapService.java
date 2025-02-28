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
import swyp_8th.bungmakase_backend.dto.bung_level.ReviewListResponse;
import swyp_8th.bungmakase_backend.dto.bung_map.*;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.repository.BungShopImageRepository;
import swyp_8th.bungmakase_backend.repository.BungShopRepository;
import swyp_8th.bungmakase_backend.repository.BungShopReviewRepository;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.time.LocalTime;
import java.util.*;
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
                        Arrays.asList(shop.getTastes().split(",")),
                        shop.getBungShopImages().stream()
                                .filter(image -> image.getBungShopReview() == null)  // bung_shop_review_id가 null인 이미지만 필터링
                                .map(BungShopImage::getImageUrl)  // 이미지 URL만 추출
                                .findFirst()  // 첫 번째 이미지만 사용
                                .orElse(null)  // 없으면 null
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
                        Arrays.asList(shop.getTastes().split(",")),
                        shop.getBungShopImages().stream()
                                .filter(image -> image.getBungShopReview() == null)  // bung_shop_review_id가 null인 이미지만 필터링
                                .map(BungShopImage::getImageUrl)  // 이미지 URL만 추출
                                .findFirst()  // 첫 번째 이미지만 사용
                                .orElse(null)  // 없으면 null
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
                        Arrays.asList(shop.getTastes().split(",")),
                        shop.getBungShopImages().stream()
                                .filter(image -> image.getBungShopReview() == null)  // bung_shop_review_id가 null인 이미지만 필터링
                                .map(BungShopImage::getImageUrl)  // 이미지 URL만 추출
                                .findFirst()  // 첫 번째 이미지만 사용
                                .orElse(null)  // 없으면 null
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

    public ShopInfoResponse getShopInfo(UUID shopId) {
        // 1. 가게 정보 조회
        BungShop shop = bungShopRepository.findById(shopId)
                .orElseThrow(() -> new NoSuchElementException("가게 정보를 찾을 수 없습니다."));

        // 2. 이미지 URL 리스트 조회
        List<BungShopImage> images = bungShopImageRepository.findByBungShopId(shopId);
        List<String> imageUrls = images.stream()
                .map(BungShopImage::getImageUrl)
                .collect(Collectors.toList());

        // 3. Taste 정보를 리스트로 변환
        List<String> tastes = Arrays.asList(shop.getTastes().split(","));

        // 4. Response DTO 생성
        ShopInfoResponse response = new ShopInfoResponse();
        response.setShopId(shop.getId().toString());
        response.setShopName(shop.getShopName());
        response.setStartTime(shop.getStartTime().toString());
        response.setEndTime(shop.getEndTime().toString());
        response.setAddress(shop.getShopAddress());
        response.setTastes(tastes);
        response.setPhone(shop.getPhoneNumber());
        response.setImageUrls(imageUrls);

        return response;
    }

    public List<ShopPhotoResponse> getShopPhotos(UUID shopId) {
        // 가게 존재 여부 확인
        BungShop shop = bungShopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청 - 유효하지 않은 shopId"));

        // 해당 가게의 이미지 리스트 조회
        List<BungShopImage> images = bungShopImageRepository.findByBungShopId(shopId);

        // DTO 변환
        return images.stream()
                .map(image -> new ShopPhotoResponse(
                        image.getId().toString(),
                        image.getImageUrl(),
                        image.getUploadedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<ReviewListResponse> getShopReviews(UUID shopId) {
        // 가게 존재 여부 확인
        BungShop shop = bungShopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청 - 유효하지 않은 shopId"));

        // 해당 가게의 리뷰 리스트 조회
        List<BungShopReview> reviews = bungShopReviewRepository.findByBungShopId(shopId);

        // DTO 변환
        return reviews.stream()
                .map(review -> new ReviewListResponse(
                        review.getId(),
                        review.getUser().getImage_url(),
                        review.getUser().getLevel(),
                        review.getUser().getNickname(),
                        review.getBungShopImages().stream()
                                .map(BungShopImage::getImageUrl)
                                .collect(Collectors.toList()),
                        review.getReviewText(),
                        review.getCreatedAt(),
                        review.getStar()
                ))
                .collect(Collectors.toList());
    }
}
