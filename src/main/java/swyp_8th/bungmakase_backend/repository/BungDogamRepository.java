package swyp_8th.bungmakase_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.domain.Users;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BungDogamRepository extends JpaRepository<BungDogam, UUID> {

    BungDogam findById(int id);

    Optional<BungDogam> findByBungName(String bungName);

}


