package swyp_8th.bungmakase_backend.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import swyp_8th.bungmakase_backend.domain.Users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    Users findUsersById(UUID uuid);

    Optional<Users> findByOauthId(String oauthId);

    // 이메일로 사용자 찾기
    Optional<Users> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // Users 객체 직접 반환 (조회 실패 시 null 반환)
    Users findByNickname(String nickname);

    List<Users> findTop20ByOrderByLevelDescRecentCountDesc();

    List<Users> findTop3ByOrderByLevelDescRecentCountDesc();

    @Transactional
    @Modifying
    @Query("UPDATE Users u SET u.recentCount = 0")
    void resetRecentCounts();

}
