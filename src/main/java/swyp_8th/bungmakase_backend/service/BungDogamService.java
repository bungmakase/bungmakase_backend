package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.repository.BungDogamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BungDogamService {

    private final BungDogamRepository bungDogamRepository;

    @Transactional(readOnly = true)
    public List<BungDogam> getAllBungDogam() {
        return bungDogamRepository.findAll();
    }
}
