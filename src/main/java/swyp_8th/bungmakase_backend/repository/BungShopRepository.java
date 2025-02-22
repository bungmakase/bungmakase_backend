package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyp_8th.bungmakase_backend.domain.BungShop;
import swyp_8th.bungmakase_backend.dto.bung_map.MarkerResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BungShopRepository extends JpaRepository<BungShop, UUID> {

    // 모든 붕어빵 가게 조회
    List<BungShop> findAll();

    // shopId로 특정 가게 조회
    Optional<BungShop> findById(UUID shopId);

    // 가게 이름으로 유사 검색 (10개까지)
    @Query("SELECT b FROM BungShop b WHERE LOWER(b.shopName) LIKE LOWER(CONCAT('%', :shopName, '%')) ORDER BY b.shopName ASC")
    List<BungShop> searchByShopName(@Param("shopName") String shopName, Pageable pageable);


}
