package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.BungShop;

import java.util.List;
import java.util.UUID;

public interface BungShopRepository extends JpaRepository<BungShop, UUID> {

    // 모든 붕어빵 가게 조회
    List<BungShop> findAll();
}
