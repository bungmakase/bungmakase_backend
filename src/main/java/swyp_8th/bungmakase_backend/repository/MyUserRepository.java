package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.Users;

import java.util.UUID;

public interface MyUserRepository extends JpaRepository<Users, UUID> {

}
