package swyp_8th.bungmakase_backend.repository;

import swyp_8th.bungmakase_backend.domain.BungDogam;

import java.util.Optional;

public interface BungDogamRepository {
    Optional<BungDogam> findByBungName(String bungName);
}
