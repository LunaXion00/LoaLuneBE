package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.leaderboard.dto.LeaderboardResponseDTO;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final ObjectMapper objectMapper;
    public List<LeaderboardResponseDTO> getLeaderboard(GameType gameType){
        List<Leaderboard> leaderboardList = leaderboardRepository.findByGameTypeOrderByRankAsc(gameType);

        return leaderboardList.stream().map(entry -> {
            return LeaderboardResponseDTO.builder()
                    .streamerName(entry.getStreamerName())
                    .rank(entry.getRank())
                    .rankChange(entry.getRankChange())
                    .itemLevel(entry.getItemLevel())
                    .build();
        }).collect(Collectors.toList());
    }
    private Map<String, Object> parseRankingDetails(String rankingDetailsJson) {
        try {
            return objectMapper.readValue(rankingDetailsJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("랭킹 상세 정보 JSON 변환 오류", e);
        }
    }
}
