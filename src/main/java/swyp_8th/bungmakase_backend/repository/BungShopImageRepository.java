package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.BungShopImage;

import java.util.List;
import java.util.UUID;

public interface BungShopImageRepository extends JpaRepository<BungShopImage, UUID> {

    List<BungShopImage> findByBungShopId(UUID shopId);
}
