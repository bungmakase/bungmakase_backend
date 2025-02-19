package swyp_8th.bungmakase_backend.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import swyp_8th.bungmakase_backend.domain.GuestSession;

import java.time.LocalDateTime;

@Repository
public interface GuestSessionRepository extends JpaRepository<GuestSession, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GuestSession gs WHERE gs.expiresAt < :currentTime")
    void deleteExpiredSessions(@Param("currentTime") LocalDateTime currentTime);
}
