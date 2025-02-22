package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.BungDogam;

import java.util.UUID;

public interface BungDogamRepository extends JpaRepository<BungDogam, UUID> {
}
