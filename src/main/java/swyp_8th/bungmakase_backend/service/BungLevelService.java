package swyp_8th.bungmakase_backend.service;

import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Service;
import swyp_8th.bungmakase_backend.api.dto.UserLevelResponseDto;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.repository.MyUserRepository;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class BungLevelService {

    private final MyUserRepository myUserRepository;

    public BungLevelService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    public UserLevelResponseDto getUserLevel(String token) {
        try {
            UUID userId = UUID.fromString(token);
            Users user = myUserRepository.findById(userId)
                    .orElseThrow(() -> new UnauthorizedException("User not Found"));
            return new UserLevelResponseDto(user.getNickname(), user.getLevel(), user.getBungCount());
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("Invalid token format");
        }

    }

}
