package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.UserBungLog;
import swyp_8th.bungmakase_backend.domain.Users;

import java.util.List;
import java.util.UUID;

public interface UserBungLogRepository extends JpaRepository<UserBungLog, UUID> {

    List<UserBungLog> findByUserOrderByLogDateDesc(Users user);
}
