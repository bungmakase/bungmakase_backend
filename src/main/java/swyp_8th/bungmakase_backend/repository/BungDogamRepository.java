package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.domain.Users;

import java.math.BigInteger;
import java.util.UUID;

public interface BungDogamRepository extends JpaRepository<BungDogam, UUID> {

    BungDogam findById(int id);
}
