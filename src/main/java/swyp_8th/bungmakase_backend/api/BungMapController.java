package swyp_8th.bungmakase_backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp_8th.bungmakase_backend.dto.bung_map.MarkerResponseDto;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.BungMapService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000", "https://localhost:3001"})
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class BungMapController {

    private final BungMapService mapService;

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
}
