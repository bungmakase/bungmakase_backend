package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swyp_8th.bungmakase_backend.domain.BungShop;
import swyp_8th.bungmakase_backend.dto.bung_map.MarkerResponseDto;
import swyp_8th.bungmakase_backend.repository.BungShopRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BungMapService {

    private final BungShopRepository bungShopRepository;

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
}
