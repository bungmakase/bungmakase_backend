package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.domain.UserBungDogam;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.repository.BungDogamRepository;
import swyp_8th.bungmakase_backend.repository.UserBungDogamRepository;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BungDogamService {

    private final BungDogamRepository bungDogamRepository;
    private final UserBungDogamRepository userBungDogamRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<BungDogam> getAllBungDogam() {
        return bungDogamRepository.findAll();
    }

    @Transactional
    public List<UserBungDogam> getUserFoundBung(UUID userId) {
        return userBungDogamRepository.findFoundBungByUserId(userId);
    }

    // 유저 ID와 붕어빵 ID로 UserBungDogam 조회
    public UserBungDogam getUserBungDogam(UUID userId, int bungId) {
        return userBungDogamRepository.findByUserIdAndBungId(userId, bungId);
    }

    @Transactional
    public UserBungDogam saveUserBungDogam(String nickname, int bungId, String tags) {
        // 1. Users 조회
        Users testUser = userRepository.findByNickname(nickname);
        if (testUser == null) {
            throw new RuntimeException("User not found with nickname: " + nickname);
        }

        // 2. BungDogam 조회
        BungDogam testBungDogam = bungDogamRepository.findById(bungId);

        // 3. UserBungDogam 생성 및 저장
        UserBungDogam userBungDogam = new UserBungDogam();
        userBungDogam.setUser(testUser);
        userBungDogam.setBung(testBungDogam);
        userBungDogam.setFound(true);
        userBungDogam.setTags(tags);

        userBungDogamRepository.save(userBungDogam);

        return userBungDogam;
    }
}
