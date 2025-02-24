package swyp_8th.bungmakase_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp_8th.bungmakase_backend.dto.profile.RankingResponseDto;
import swyp_8th.bungmakase_backend.dto.bung_level.UserLevelResponseDto;
import swyp_8th.bungmakase_backend.domain.Users;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BungLevelService {

    private final UserRepository userRepository;


    public UserLevelResponseDto getUserLevel(UUID userId) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new UnauthorizedException("User not Found"));
            return new UserLevelResponseDto(user.getNickname(), user.getLevel(), user.getBungCount());
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("Invalid token format");
        }

    }

    public List<RankingResponseDto> get20Rankings() {

        List<Users> topUsers = userRepository.findTop20ByOrderByLevelDescRecentCountDesc();
        List<RankingResponseDto> rankingList = new ArrayList<>();

        long displayRank = 1L; // 화면에 표시할 순위
        Long previousCount = null;

        for (Users user : topUsers) {
            Long currentCount = user.getRecentCount();

            if (previousCount == null || currentCount.equals(previousCount)) {
                rankingList.add(new RankingResponseDto(displayRank, user.getNickname(), user.getLevel(), currentCount));
                previousCount = currentCount;
            }

            else{
                displayRank+=1;
                rankingList.add(new RankingResponseDto(displayRank, user.getNickname(), user.getLevel(), currentCount));
                previousCount = currentCount;
            }

        }

        return rankingList;
    }

    public List<RankingResponseDto> getTop3() {

        List<Users> topUsers = userRepository.findTop3ByOrderByLevelDescRecentCountDesc();
        List<RankingResponseDto> rankingList = new ArrayList<>();

        long displayRank = 1L; // 화면에 표시할 순위
        Long previousCount = null;

        for (Users user : topUsers) {
            Long currentCount = user.getRecentCount();

            if (previousCount == null || currentCount.equals(previousCount)) {
                rankingList.add(new RankingResponseDto(displayRank, user.getNickname(), user.getLevel(), currentCount));
                previousCount = currentCount;
            }

            else{
                displayRank+=1;
                rankingList.add(new RankingResponseDto(displayRank, user.getNickname(), user.getLevel(), currentCount));
                previousCount = currentCount;
            }

        }

        return rankingList;
    }

}
