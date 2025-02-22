package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp_8th.bungmakase_backend.domain.BungShop;
import swyp_8th.bungmakase_backend.dto.bung_map.MarkerResponseDto;
import swyp_8th.bungmakase_backend.repository.BungShopRepository;

import java.util.Arrays;
import java.util.List;
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
}
