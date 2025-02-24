package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_8th.bungmakase_backend.domain.BungShopReview;

import java.util.UUID;

@Repository
public interface BungShopReviewRepository extends JpaRepository<BungShopReview, UUID> {
}
