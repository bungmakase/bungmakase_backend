package swyp_8th.bungmakase_backend.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_8th.bungmakase_backend.domain.Users;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByOauthId(String oauthId);

    // 이메일로 사용자 찾기
    Optional<Users> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // 1) Users 객체 직접 반환 (조회 실패 시 null 반환)
    Users findByNickname(String nickname);

}
