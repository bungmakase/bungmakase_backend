package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByOauthId(String oauthId);

    // 이메일로 사용자 찾기
    Optional<Users> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
