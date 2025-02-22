package swyp_8th.bungmakase_backend;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.domain.UserBungDogam;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.repository.BungDogamRepository;
import swyp_8th.bungmakase_backend.repository.UserBungDogamRepository;
import swyp_8th.bungmakase_backend.repository.UserRepository;
import swyp_8th.bungmakase_backend.service.BungDogamService;

@SpringBootTest
@Transactional
@Rollback(false)
public class UserBungDogamServiceTest {


    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private BungDogamRepository bungDogamRepository;

    @Autowired
    private UserBungDogamRepository userBungDogamRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("UserBungDogam 저장 테스트")
    void testSaveAndRetrieveUserBungDogam() {
        // 1. 테스트용 Users 생성 및 저장
        Users testUser = usersRepository.findByNickname("jdoeun");

        // 2. 테스트용 BungDogam
        BungDogam testBungDogam = bungDogamRepository.findById(1);

        // 3. UserBungDogam 생성 및 저장
        UserBungDogam userBungDogam = new UserBungDogam();
        userBungDogam.setUser(testUser);
        userBungDogam.setBung(testBungDogam);
        userBungDogam.setFound(true);

        userBungDogamRepository.save(userBungDogam);
        entityManager.flush();
    }
}
