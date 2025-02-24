package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.UserBungImage;

import java.util.UUID;

public interface UserBungImageRepository extends JpaRepository<UserBungImage, UUID> {

}
