package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.Users;

import java.util.List;
import java.util.UUID;

public interface MyUserRepository extends JpaRepository<Users, UUID> {
    //Get top 20 people sorted by descending level and, if same level, descending recentCount
    List<Users> findTop20ByOrderByLevelDescRecentCountDesc();

}
