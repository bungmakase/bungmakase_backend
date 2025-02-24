package swyp_8th.bungmakase_backend.service;

import org.springframework.stereotype.Service;
import swyp_8th.bungmakase_backend.dto.profile.RankingResponseDto;
import swyp_8th.bungmakase_backend.dto.bung_level.UserLevelResponseDto;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.repository.MyUserRepository;

import java.util.ArrayList;
import java.util.List;
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

    public List<RankingResponseDto> getRankings() {
        // View the top 20 users by level and recent fish-bun
        List<Users> topUsers = myUserRepository.findTop20ByOrderByLevelDescRecentCountDesc();
        List<RankingResponseDto> rankingList = new ArrayList<>();

        int rank = 1;
        for (Users user : topUsers) {
            // 여기서는 Users 엔티티의 recentCount 필드를 총 먹은 붕어빵 수로 사용합니다.
            rankingList.add(new RankingResponseDto(rank, user.getNickname(), user.getLevel(), user.getRecentCount()));
            rank++;
        }
        return rankingList;
    }



}
