package swyp_8th.bungmakase_backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp_8th.bungmakase_backend.config.JwtConfig;
import swyp_8th.bungmakase_backend.dto.bung_level.ReviewListResponse;
import swyp_8th.bungmakase_backend.dto.bung_map.*;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.BungMapService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class BungMapController {

    private final BungMapService mapService;
    private final JwtConfig jwtConfig;

    @GetMapping("/markers")
    public ResponseEntity<ResponseTemplate<List<MarkerResponseDto>>> getAllMarkers() {
        try {
            List<MarkerResponseDto> markers = mapService.getAllMarkers();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, markers));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @GetMapping("/markers/search/name")
    public ResponseEntity<ResponseTemplate<List<MarkerResponseDto>>> searchMarkers(
            @RequestParam("shopName") String shopName) {

        try {
            List<MarkerResponseDto> markers = mapService.searchMarkersByShopName(shopName);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, markers));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @GetMapping("/markers/search")
    public ResponseEntity<ResponseTemplate<List<MarkerResponseDto>>> getMarkerByShopId(
            @RequestParam("shopId") UUID shopId) {

        try {
            Optional<MarkerResponseDto> markerOpt = mapService.getMarkerByShopId(shopId);

            if (markerOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, List.of(markerOpt.get())));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseTemplate<>(FailureCode.NOT_FOUND_404, null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @PostMapping(value = "/reviews",  consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseTemplate<Void>> addShopReview(
            @CookieValue("token") String token,
            @RequestPart("reviewData") ShopReviewRequest reviewData,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {

        try {
            UUID userId = jwtConfig.getUserIdFromToken(token);

            mapService.addShopReview(userId, reviewData, images);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseTemplate<>(SuccessCode.CREATED_201, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        }
    }

    @PostMapping(value = "/shops",  consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseTemplate<AddShopResponse>> addShop(
            @RequestPart("shopData") AddShopRequest shopData,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {

        try {

            AddShopResponse response = mapService.addNewShop(shopData, images);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseTemplate<>(SuccessCode.CREATED_201, response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        }
    }

    @GetMapping("/home")
    public ResponseEntity<ResponseTemplate<ShopInfoResponse>> getShopHome(@RequestParam("shopId") UUID shopId) {
        try {
            // 1. 유효성 검사
            if (shopId == null) {
                return ResponseEntity.badRequest()
                        .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
            }

            // 2. 서비스 호출
            ShopInfoResponse shopInfo = mapService.getShopInfo(shopId);

            return ResponseEntity.ok()
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, shopInfo));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseTemplate<>(FailureCode.NOT_FOUND_404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @GetMapping("/home/photos")
    public ResponseEntity<ResponseTemplate<List<ShopPhotoResponse>>> getShopPhotos(@RequestParam("shopId") UUID shopId) {
        try {
            // 유효성 검사
            if (shopId == null) {
                return ResponseEntity.badRequest()
                        .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
            }

            // 서비스 호출
            List<ShopPhotoResponse> photos = mapService.getShopPhotos(shopId);

            return ResponseEntity.ok()
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, photos));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }

    @GetMapping("/home/reviews")
    public ResponseEntity<ResponseTemplate<List<ReviewListResponse>>> getShopReviews(@RequestParam("shopId") UUID shopId) {
        try {
            // 유효성 검사
            if (shopId == null) {
                return ResponseEntity.badRequest()
                        .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
            }

            // 서비스 호출
            List<ReviewListResponse> reviews = mapService.getShopReviews(shopId);

            return ResponseEntity.ok()
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, reviews));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate<>(FailureCode.BAD_REQUEST_400, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }
}
