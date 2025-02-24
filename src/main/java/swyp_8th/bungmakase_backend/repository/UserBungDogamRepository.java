package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp_8th.bungmakase_backend.domain.UserBungDogam;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBungDogamRepository extends JpaRepository<UserBungDogam, UUID> {

    @Query("SELECT ubd FROM UserBungDogam ubd WHERE ubd.user.id = :userId AND ubd.found = true")
    List<UserBungDogam> findFoundBungByUserId(@Param("userId") UUID userId);

    // 유저 ID와 Bung ID로 특정 UserBungDogam 조회
    @Query("SELECT ubd FROM UserBungDogam ubd WHERE ubd.user.id = :userId AND ubd.bung.id = :bungId")
    UserBungDogam findByUserIdAndBungId(@Param("userId") UUID userId, @Param("bungId") int bungId);

    Optional<UserBungDogam> findByUserIdAndBungId(UUID userId, BigInteger bungId);
}
